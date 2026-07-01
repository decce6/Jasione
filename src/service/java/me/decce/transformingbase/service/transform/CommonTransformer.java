package me.decce.transformingbase.service.transform;

import me.decce.transformingbase.core.Jasione;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.ArrayList;
import java.util.List;

public class CommonTransformer {
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean process(ClassNode node) {
        if (processInner(node)) {
            maybeDumpClass(node);
            return true;
        }
        return false;
    }

    private static void maybeDumpClass(ClassNode classNode) {
        if (Jasione.getConfig().dumpClasses) {
            dumpClass(classNode);
        }
    }

    private static void dumpClass(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        ClassDumper.dump(classNode.name, writer.toByteArray());
    }

    public static boolean processInner(ClassNode node) {
        boolean transformed = false;
        // Copy the method list, because during processing we might need to add clinit if it does not exist
        var methodNodes = List.copyOf(node.methods);
        for (var methodNode : methodNodes) {
            // Process all methods, return true if any of them has been transformed
            if (processMethod(methodNode, node)) {
                transformed = true;
            }
        }
        return transformed;
    }

    public static boolean processMethod(MethodNode node, ClassNode classNode) {
        if (ASMUtil.CLINIT.equals(node.name)) {
            // Doing `VALUES = values()` is common in <clinit>, we shouldn't optimize this values() call
            // Because static constructors only run once, they are not worth optimizing anyway
            return false;
        }
        if (node.instructions == null || node.instructions.size() == 0) {
            return false;
        }
        return rewriteSafeValuesCall(node, classNode);
    }

    private static boolean rewriteSafeValuesCall(MethodNode node, ClassNode classNode) {
        List<MethodInsnNode> valuesInsns = new ArrayList<>();
        for (var insn : node.instructions) {
            if (insn instanceof MethodInsnNode methodInsnNode &&
                    isLikelyEnumValuesCall(methodInsnNode)) {
                valuesInsns.add(methodInsnNode);
            }
        }

        boolean rewritten = false;
        for (var valuesInsn : valuesInsns) {
            if (isSafe(node, classNode.name, valuesInsn)) {
                processSafeValuesCall(valuesInsn, node, classNode);
                rewritten = true;
            }
        }
        return rewritten;
    }

    private static boolean isSafe(MethodNode node, String className, MethodInsnNode valuesInsn) {
        InterpreterEx interpreterEx = new InterpreterEx(valuesInsn);
        Analyzer<SourceValueEx> analyzer = new Analyzer<>(interpreterEx);
        try {
            analyzer.analyze(className, node);
        } catch (AnalyzerException e) {
            LOGGER.warn("Failed to analyze {}", className, e);
            return false;
        }
        return !interpreterEx.hasViolation();
    }

    private static void processSafeValuesCall(MethodInsnNode valuesInsn, MethodNode methodNode, ClassNode classNode) {
        String cacheClassInternalName = TransformationConstants.cacheClassName(valuesInsn.owner);
        String cacheClassName = cacheClassInternalName.replace('/', '.');
        String arrayDesc = valuesInsn.desc.substring(2); // ()[LEnum; -> [LEnum;

        // Step 1: change the values() call to use the cached values() method in the cache class
        var insnList = new InsnList();
        // The call will be inlined by the JIT, and the type cast will be eliminated.
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, cacheClassInternalName, TransformationConstants.VALUES_METHOD_NAME, ASMUtil.DESC_METHOD_OBJECT_ARRAY));
        insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, arrayDesc));
        methodNode.instructions.insert(valuesInsn, insnList);
        methodNode.instructions.remove(valuesInsn);

        // Step 2: generate the cache holder class, if not exists
        var classloader = Thread.currentThread().getContextClassLoader();
        if (!ReflectionUtil.classExists(classloader, cacheClassName)) {
            byte[] bytes = CacheClassGenerator.generateFor(valuesInsn.owner, classNode.version);
            ReflectionUtil.defineClass(classloader, cacheClassName, bytes);
        }
    }

    public static boolean isLikelyEnumValuesCall(MethodInsnNode insn) {
        // We accept false positives. A call that satisfy these conditions are likely, but not necessarily,
        //  Enum#values() calls. These false positives are handled correctly (no semantic change) in our later
        //  transformation.
        return insn.getOpcode() == Opcodes.INVOKESTATIC &&
                insn.name.equals(TransformationConstants.VALUES_METHOD_NAME) &&
                insn.desc.equals("()[L" + insn.owner + ";");
    }
}
