package me.decce.transformingbase.service.transform;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ASMUtil {
    public static final String CLINIT = "<clinit>";
    public static final String DESC_METHOD_OBJECT_ARRAY = "()[Ljava/lang/Object;";
    public static final String DESC_OBJECT_ARRAY = "[Ljava/lang/Object;";
    public static final String DESC_BOOLEAN = "Z";
    public static final String OBJECT_INTERNAL_NAME = Type.getInternalName(Object.class);
    public static final String ILLEGAL_ACCESS_ERROR_INTERNAL_NAME = Type.getInternalName(IllegalAccessError.class);
    public static final int ACC_PUBLIC_FINAL = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;
    public static final int ACC_PUBLIC_STATIC = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
    public static final int ACC_PUBLIC_STATIC_FINAL = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
    public static final int ACC_SYNTHETIC_PUBLIC_STATIC = Opcodes.ACC_SYNTHETIC | ACC_PUBLIC_STATIC;
    public static final int ACC_SYNTHETIC_PUBLIC_FINAL = Opcodes.ACC_SYNTHETIC | ACC_PUBLIC_FINAL;
    public static final int ACC_SYNTHETIC_PUBLIC_STATIC_FINAL = Opcodes.ACC_SYNTHETIC | ACC_PUBLIC_STATIC_FINAL;
}
