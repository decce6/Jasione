package me.decce.transformingbase.service.transform;

import me.decce.transformingbase.core.Jasione;
import net.lenni0451.reflect.Classes;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;

// Accessed from generated class. See CacheClassGenerator.java
@SuppressWarnings("unused")
public class EnumValuesAccessor {
    public static String INTERNAL_NAME = Type.getInternalName(EnumValuesAccessor.class);

    private static boolean isExtensibleEnum(Class<?> enumClass) {
        // Forge (and thus Kilt) can overwrite the values array if the enum class is already loaded at the time a mod requests enum extension
        // We must exclude all `IExtensibleEnum`s from optimization to prevent issues - see
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
        } catch (NoClassDefFoundError ignored) {
            // This might happen if the mod optionally depends on another mod
            // See: https://github.com/decce6/Jasione/issues/9
            return null;
        }
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
