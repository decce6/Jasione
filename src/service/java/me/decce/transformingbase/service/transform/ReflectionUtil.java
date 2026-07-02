package me.decce.transformingbase.service.transform;

import net.lenni0451.reflect.ClassLoaders;
import net.lenni0451.reflect.JavaBypass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MethodHandle IMPL_ADD_READS_ALL_UNNAMED = unreflect(() -> Module.class.getDeclaredMethod("implAddReadsAllUnnamed"));

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytes) {
        return ClassLoaders.defineClass(classLoader, name, bytes);
    }

    public static void addReadsAllUnnamed(Module module) {
        try {
            IMPL_ADD_READS_ALL_UNNAMED.invokeExact(module);
        } catch (Throwable e) {
            LOGGER.warn("Failed to add reads to module {}", module.getName(), e);
        }
    }

    public static boolean classExists(ClassLoader classLoader, String name) {
        try {
            Class.forName(name, false, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static MethodHandle unreflect(UncheckedSupplier<Method, ?> method) {
        try {
            return JavaBypass.TRUSTED_LOOKUP.unreflect(method.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle unreflectGetter(UncheckedSupplier<Field, ?> field) {
        try {
            return JavaBypass.TRUSTED_LOOKUP.unreflectGetter(field.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface UncheckedSupplier<T, E extends Throwable> {
        T get() throws E;
    }
}
