package me.decce.transformingbase.service.transform;

import net.lenni0451.reflect.Classes;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;

// Accessed from generated class. See CacheClassGenerator.java
@SuppressWarnings("unused")
public class EnumValuesAccessor {
    public static String INTERNAL_NAME = Type.getInternalName(EnumValuesAccessor.class);

    public static boolean isEnum(String enumClassName) {
        var enumClass = Classes.byName(enumClassName, Classes.getCallerClass(1).getClassLoader());
        return enumClass != null && enumClass.isEnum();
    }

    public static Object[] values(String enumClassName) {
        var enumClass = Classes.byName(enumClassName, Classes.getCallerClass(1).getClassLoader());
        if (enumClass == null || !enumClass.isEnum()) {
            return null;
        }
        return enumClass.getEnumConstants();
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
