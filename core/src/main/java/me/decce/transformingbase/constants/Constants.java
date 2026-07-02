package me.decce.transformingbase.constants;

import java.nio.file.Path;

public class Constants {
    public static final String MOD_ID = "jasione";
    public static final String MOD_NAME = "Jasione";
    public static final String CORE_PACKAGE = "me.decce." + MOD_ID + ".core";
    public static final String FORGE_JIJ_NAME = MOD_ID + "-forge-mod.jar";
    public static final String NATIVE_METHOD_PREFIX = MOD_ID + "_wrapped$";
    public static final String OUTPUT_DIR = "." + MOD_ID + ".out";
    public static final Path OUTPUT_PATH = Path.of(OUTPUT_DIR);
}
