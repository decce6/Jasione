package me.decce.transformingbase.service.transform;

//? forge || (neoforge && <1.21.10) {
/*import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import java.lang.invoke.MethodHandle;
*///? }
//? neoforge && >=1.21.10 {
/*import net.neoforged.fml.loading.FMLLoader;
 *///? }

public class TransformationConstants {
    public static final String VALUES_METHOD_NAME = "values";
    //? fabric {
    private static final ClassLoader THIS_CLASS_LOADER = CommonTransformer.class.getClassLoader();
     //? }
    //? forge || (neoforge && <1.21.10) {
    /*public static final MethodHandle TRANSFORMING_CLASSLOADER_GETTER = ReflectionUtil.unreflectGetter(() -> Launcher.class.getDeclaredField("classLoader"));
    *///? }

    public static String cacheClassName(String enumClass) {
        return "me/decce/jasione/cached/" + enumClass;
    }

    public static ClassLoader findClassLoaderForCacheClass() {
        // Previously, we relied on the thread context classloader, which may be manipulated or simply does not exist
        // See: https://github.com/decce6/Jasione/issues/3 and https://github.com/decce6/Jasione/issues/4
        //? fabric {
        // On Fabric, we will be loaded by knot, so we just use the classloader for this class
        return THIS_CLASS_LOADER;
        //? } else if neoforge && >=1.21.10 {
        /*return FMLLoader.getCurrent().getCurrentClassLoader();
         *///? } else {
        /*try {
            return (TransformingClassLoader) TRANSFORMING_CLASSLOADER_GETTER.invokeExact(Launcher.INSTANCE);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        *///? }
    }
}
