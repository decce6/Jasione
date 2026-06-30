package me.decce.transformingbase.service.fabric;

//? if fabric {
import me.decce.transformingbase.core.Jasione;
import me.decce.transformingbase.service.transform.CommonTransformer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.lenni0451.reflect.ClassLoaders;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class FabricPreLaunchEntrypoint implements PreLaunchEntrypoint {
    @SuppressWarnings("unchecked")
    @Override
    public void onPreLaunch() {
        Jasione.getConfig(); // Initialize the config earlier to prevent infinite loop during transformation
        var transformer = MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
        try {
            var processorField = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer").getDeclaredField("processor");
            processorField.setAccessible(true);
            var processor = processorField.get(transformer);
            var coprocessorsField = Class.forName("org.spongepowered.asm.mixin.transformer.MixinProcessor").getDeclaredField("coprocessors");
            coprocessorsField.setAccessible(true);
            ArrayList<Object> coprocessors = (ArrayList<Object>) coprocessorsField.get(processor);
            coprocessors.add(createCoprocessor(MixinEnvironment.class.getClassLoader(), CommonTransformer::process));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Object createCoprocessor(ClassLoader classLoader, Predicate<ClassNode> predicate) throws Throwable {
        String className = "org.spongepowered.asm.mixin.transformer.JasioneMixinCoprocessor";
        try (var res = this.getClass().getResourceAsStream("/" + className.replace('.', '/') + ".class")) {
            var bytes = Objects.requireNonNull(res).readAllBytes();
            var clazz = ClassLoaders.defineClass(classLoader, className, bytes);
            return clazz.getConstructor(Predicate.class).newInstance(predicate);
        }
    }
}
//?}
