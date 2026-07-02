package me.decce.transformingbase.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class JasioneConfig {
    @Comment("Specifies whether to enable the mod")
    public boolean enabled = true; //TODO
    @Comment("")
    public List<String> knownEnums = List.of();
    @Key("debug.dumpClasses")
    @Comment("")
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
