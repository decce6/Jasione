package me.decce.transformingbase.service.transform;

import org.objectweb.asm.tree.MethodInsnNode;

public record MethodHolder(String owner, String name, String descriptor) {
    public static MethodHolder of(MethodInsnNode node) {
        return new MethodHolder(node.owner, node.name, node.desc);
    }
}
