package me.decce.transformingbase.service.transform;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;

public class ASMUtil {
    public static final String CLINIT = "<clinit>";

    public static MethodNode clinitOf(ClassNode node) {
        return node.methods.stream()
                .filter(m -> CLINIT.equals(m.name))
                .findFirst()
                .orElseGet(() -> {
                    var m = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                    m.instructions.insert(new InsnNode(Opcodes.RETURN));
                    node.methods.add(m);
                    return m;
                });
    }

    public static AbstractInsnNode[] findReturnInsns(MethodNode m) {
        var list = new ArrayList<AbstractInsnNode>();
        for (var insn = m.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() == Opcodes.RETURN) {
                list.add(insn);
            }
        }
        return list.toArray(new AbstractInsnNode[0]);
    }
}
