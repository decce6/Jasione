package org.spongepowered.asm.mixin.transformer;

import me.decce.transformingbase.constants.Constants;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Predicate;

public class JasioneMixinCoprocessor extends MixinCoprocessor {
    private final Predicate<ClassNode> predicate;

    public JasioneMixinCoprocessor(Predicate<ClassNode> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean couldTransform(String className) {
        return !className.startsWith("me.decce.jasione.service");
    }

    @Override
    String getName() {
        return Constants.MOD_ID;
    }

    @Override
    boolean postProcess(String className, ClassNode classNode) {
        return predicate.test(classNode);
    }
}
