package me.decce.transformingbase.service.transform;

public class TransformationConstants {
    public static final String VALUES_METHOD_NAME = "values";

    public static String cacheClassName(String enumClass) {
        return "me/decce/jasione/cached/" + enumClass;
    }
}
