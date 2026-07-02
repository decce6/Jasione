package me.decce.transformingbase.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class JasioneConfig {
    @Comment("Specifies whether to enable the mod")
    public boolean enabled = true;
    @Key("debug.printOptimization")
    @Comment("When enabled, logs when optimizations are applied")
    public boolean printOptimization;
    @Key("debug.dumpClasses")
    @Comment("When enabled, dumps transformed classes to \".jasione.out\" folder")
    public boolean dumpClasses;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Comment {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Key {
        String value();
    }
}
