package me.decce.transformingbase.service.transform;

import me.decce.transformingbase.core.Jasione;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class CacheClassGenerator {
    /*
    * public final class GeneratedClass {
    *     public static final Object[] VALUES;
    *     public static final boolean IS_ENUM;
    *
    *     static {...}
    *
    *     public static Object[] values() {
    *         // Both VALUES and IS_ENUM are static final - both the branching and the type cast will be eliminated by the JIT
    *         if (IS_ENUM) {
    *             return VALUES;
    *         } else {
    *             // SomeEnum is not actually an enum class (we falsely identified it because the method desc is identical to one on enum) - just call values() method like original
    *             // The returned array will be cast to Object[] then back to SomeEnum[] again. And the JIT is likely to eliminate both casts altogether.
    *             return invokeOriginalValues();
    *         }
    *     }
    *
    *     public static Object[] invokeOriginalValues() {
    *         // We don't know if the original value is accessible or not, so try first, and if it fails, use reflection
    *         // As long as we don't use the private type directly here, the verifier would be fine.
    *         try {
    *             return SomeEnum.values();
    *         } catch (IllegalAccessError) {
    *             // Fallback to reflection. This should happen extremely rarely.
    *             return EnumValuesAccessor.invokeValuesSlow(...);
    *         }
    *     }
    * }
    *
    * Note on performance: at runtime, the IS_ENUM will be constant folded to eliminate the branching. The values() method is also
    *  likely to be inlined. And the type cast, too, will be optimized out.
    * TODO: There is still an edge case we don't cover. If we falsely identified a values() call that is not on an
    *  enum class, we'll go into the else branch here. Then, if SomeEnum is private, it will crash. Nevertheless, this
    *  case is too rare to be worth caring about.
    * */
    public static byte[] generateFor(String enumClass, int version) {
        String cacheClassName = TransformationConstants.cacheClassName(enumClass);

        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        classNode.version = version;
        classNode.name = cacheClassName;
        classNode.superName = Type.getInternalName(Object.class);
        classNode.access = ASMUtil.ACC_PUBLIC_FINAL;

        var valuesField = new FieldNode(ASMUtil.ACC_PUBLIC_STATIC_FINAL, "VALUES", ASMUtil.DESC_OBJECT_ARRAY, null, null);
        classNode.fields.add(valuesField);
        var isEnumField = new FieldNode(ASMUtil.ACC_PUBLIC_STATIC_FINAL, "IS_ENUM", ASMUtil.DESC_BOOLEAN, null, null);
        classNode.fields.add(isEnumField);

        // Use reflection to get values array and store the cache array as Object[] because the enum class may be inaccessible (private or package-private)
        // The reflection logic is in EnumValuesAccessor
        MethodNode clinit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        classNode.methods.add(clinit);

        /* VALUES = EnumValuesAccessor.values(...) */
        clinit.instructions.add(new LdcInsnNode(enumClass.replace('/', '.')));
        clinit.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EnumValuesAccessor.INTERNAL_NAME, "values", "(Ljava/lang/String;)[Ljava/lang/Object;"));
        clinit.instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, cacheClassName, "VALUES", ASMUtil.DESC_OBJECT_ARRAY));

        /* IS_ENUM = EnumValuesAccessor.isEnum(...) */
        clinit.instructions.add(new LdcInsnNode(enumClass.replace('/', '.')));
        clinit.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EnumValuesAccessor.INTERNAL_NAME, "isEnum", "(Ljava/lang/String;)Z"));
        clinit.instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, cacheClassName, "IS_ENUM", ASMUtil.DESC_BOOLEAN));

        clinit.instructions.add(new InsnNode(Opcodes.RETURN));

        clinit.maxStack = 1;
        clinit.maxLocals = 0;

        MethodNode values = new MethodNode(ASMUtil.ACC_PUBLIC_STATIC, "values", ASMUtil.DESC_METHOD_OBJECT_ARRAY, null, null);
        classNode.methods.add(values);

        LabelNode elseLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        values.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, cacheClassName, "IS_ENUM", "Z"));
        values.instructions.add(new JumpInsnNode(Opcodes.IFEQ, elseLabel)); // if (!IS_ENUM)
        values.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, cacheClassName, "VALUES", ASMUtil.DESC_OBJECT_ARRAY));
        values.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabel));
        values.instructions.add(elseLabel);
        values.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        values.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, cacheClassName, "invokeOriginalValues", ASMUtil.DESC_METHOD_OBJECT_ARRAY));
        values.instructions.add(endLabel);
        values.instructions.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{ASMUtil.DESC_OBJECT_ARRAY}));
        values.instructions.add(new InsnNode(Opcodes.ARETURN));
        values.maxStack = 1;
        values.maxLocals = 0;

        MethodNode invokeOriginalValues = new MethodNode(ASMUtil.ACC_PUBLIC_STATIC, "invokeOriginalValues", ASMUtil.DESC_METHOD_OBJECT_ARRAY, null, null);
        classNode.methods.add(invokeOriginalValues);

        LabelNode tryStartLabel = new LabelNode();
        LabelNode tryEndLabel = new LabelNode();
        LabelNode catchLabel = new LabelNode();
        LabelNode endLabel1 = new LabelNode();
        invokeOriginalValues.instructions.add(tryStartLabel);
        invokeOriginalValues.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, enumClass, "values", "()[L" + enumClass + ";"));
        invokeOriginalValues.instructions.add(tryEndLabel);
        invokeOriginalValues.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabel1));
        invokeOriginalValues.instructions.add(catchLabel);
        invokeOriginalValues.instructions.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{Type.getInternalName(IllegalAccessError.class)}));
        invokeOriginalValues.instructions.add(new InsnNode(Opcodes.POP));
        invokeOriginalValues.instructions.add(new LdcInsnNode(enumClass.replace('/', '.')));
        invokeOriginalValues.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EnumValuesAccessor.INTERNAL_NAME, "invokeValuesSlow", "(Ljava/lang/String;)[Ljava/lang/Object;"));
        invokeOriginalValues.instructions.add(endLabel1);
        invokeOriginalValues.instructions.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{"[Ljava/lang/Object;"}));
        invokeOriginalValues.instructions.add(new InsnNode(Opcodes.ARETURN));

        invokeOriginalValues.tryCatchBlocks.add(new TryCatchBlockNode(tryStartLabel, tryEndLabel, catchLabel, Type.getInternalName(IllegalAccessError.class)));

        invokeOriginalValues.maxStack = 1;
        invokeOriginalValues.maxLocals = 0;

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        var bytes = writer.toByteArray();

        if (Jasione.getConfig().dumpClasses) {
            ClassDumper.dump(cacheClassName, bytes);
        }

        return bytes;
    }

}
