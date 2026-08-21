package me.decce.transformingbase.service.transform;

import me.decce.transformingbase.core.Jasione;
import net.lenni0451.reflect.Classes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

// Accessed from generated class. See CacheClassGenerator.java
@SuppressWarnings("unused")
public class EnumValuesAccessor {
    public static String INTERNAL_NAME = Type.getInternalName(EnumValuesAccessor.class);

    private static boolean isExtensibleEnum(Class<?> enumClass) {
        // Forge (and thus Kilt) can overwrite the values array if the enum class is already loaded at the time a mod requests enum extension
        // We must exclude all `IExtensibleEnum`s from optimization to prevent issues - see https://github.com/decce6/Jasione/issues/7
        // This is not an issue on NeoForge because it directly transforms the enum class.
        var interfaces = enumClass.getInterfaces();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < interfaces.length; i++) {
            if ("net.minecraftforge.common.IExtensibleEnum".equals(interfaces[i].getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEnum(String enumClassName) {
        var enumClass = Classes.byName(enumClassName, Classes.getCallerClass(1).getClassLoader());
        if (enumClass == null) {
            return false;
        }
        if (isExtensibleEnum(enumClass)) {
            if (Jasione.config.printOptimization) {
                Jasione.LOGGER.info("Rejected optimization of {} because it is dynamically extensible", enumClass.getName().replace('.', '/'));
            }
            return false;
        }
        return enumClass.isEnum();
    }

    public static Object[] values(String enumClassName) {
        var enumClass = Classes.byName(enumClassName, Classes.getCallerClass(1).getClassLoader());
        if (enumClass == null || !enumClass.isEnum()) {
            return null;
        }
        try {
            return enumClass.getEnumConstants();
        } catch (Throwable ignored) {
            /* This might happen in various cases:
             * - the mod optionally depends on another mod (NoClassDefFoundError)
             *   - See: https://github.com/decce6/Jasione/issues/9 (Journeymap Integration has optional integration with various mods)
             * - the enum class contains a method whose signature uses a class which does not exist at runtime
             *   - This may be NoClassDefFoundError, or, on NeoForge, RuntimeException: Attempted to load class net/minecraft/client/renderer/RenderType for invalid dist DEDICATED_SERVER
             *   - The reason for this is that `Class#getEnumConstants` uses `Class#getMethod` to find the values method. `Class#getMethod` internally iterates through all methods, which triggers the error.
             *   - See: https://github.com/decce6/Jasione/issues/14 (com.supermartijn642.scarecrowsterritory.ScarecrowType is an enum, but contains a method whose return type is class_1921 - a client class that does not exist on dedicated servers)
             * In the first case, it is fine to just return null, as the code is likely unreachable when the optional dependency does not exist. In the second case, however, we must use an alternative way to find the values array.
             */
            return valuesFragile(enumClass);
        }
    }

    private static Object[] valuesFragile(Class<?> enumClass) {
        // This method is only reached if the class **IS** an enum, and `Class#getEnumConstants` throws an error.
        // Try to find the internal $VALUES field - fragile, because the naming is arbitrarily decided by the compiler.
        Field values = null;
        Field firstSynthetic = null;
        for (Field field : enumClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !field.isSynthetic() || !field.getType().isArray() || !field.getType().getComponentType().equals(enumClass)) {
                continue;
            }
            String name = field.getName();
            // Use startsWith instead of equals, because if the $VALUES field name is already defined by a user field, javac attempts alternate names like $VALUES$, $VALUES$$, etc.
            // The ECJ (Eclipse Compiler for Java) uses ENUM$VALUES
            if (name.startsWith("$VALUES") || "ENUM$VALUES".equals(name)) {
                values = field;
                break;
            }
            else if (firstSynthetic == null) {
                firstSynthetic = field; // Store the first synthetic field matching the type, in case there is no field matching the name, either due to obfuscation or different compiler
            }
        }
        if (values == null) {
            // Couldn't find the values field based on name, fallback to first synthetic field matching the type
            values = firstSynthetic;
        }
        if (values != null) {
            values.setAccessible(true);
            try {
                return (Object[]) values.get(null);
            } catch (IllegalAccessException ignored) {}
        }
        return null;
    }

    public static Object[] invokeValuesSlow(String nonEnumClassName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Note: this code path is only accessed when both of these conditions satisfy (very, very rare):
        //  - there is a values() call that looks exactly like it is on an enum class, but it's not (static, return type is array of self class, no params)
        //  - the enum class or the value() method is inaccessible (private / package-private)
        var nonEnumClass = Classes.byName(nonEnumClassName, Classes.getCallerClass(1).getClassLoader());
        var method = nonEnumClass.getDeclaredMethod("values");
        method.setAccessible(true);
        return (Object[]) method.invoke(null);
    }
}
