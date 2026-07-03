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
    @Key("debug.printAnalysisFailure")
    @Comment("When enabled, prints stacktrace when bytecode analysis fails. These failures are harmless and can be safely dismissed, but the failed methods will not be optimized.\n" +
            "It is recommended that you keep this enabled and report failures to the issue tracker of Jasione.")
    public boolean printFailure = true;
    @Key("debug.printOptimization")
    @Comment("When enabled, logs statistics when optimizations are applied")
    public boolean printOptimization;
    @Key("debug.dumpClasses")
    @Comment("When enabled, dumps transformed classes to the \".jasione.out\" folder")
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
