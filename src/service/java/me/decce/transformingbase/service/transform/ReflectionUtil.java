package me.decce.transformingbase.service.transform;

import net.lenni0451.reflect.ClassLoaders;

public class ReflectionUtil {
    public static void defineClass(ClassLoader classLoader, String name, byte[] bytes) {
        ClassLoaders.defineClass(classLoader, name, bytes);
    }

    public static boolean classExists(ClassLoader classLoader, String name) {
        try {
            Class.forName(name, false, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
