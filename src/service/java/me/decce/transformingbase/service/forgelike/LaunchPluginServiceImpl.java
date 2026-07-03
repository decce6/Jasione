//? forge || (neoforge && <1.21.9) {
/*package me.decce.transformingbase.service.forgelike;

import cpw.mods.cl.ModuleClassLoader;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.service.transform.CommonTransformer;
import me.decce.transformingbase.service.transform.ReflectionUtil;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.lang.invoke.MethodHandle;
import java.lang.module.ResolvedModule;
import java.util.EnumSet;
import java.util.Map;

public class LaunchPluginServiceImpl implements ILaunchPluginService {
    public static final String CLASSLOADING_REASON = ITransformerActivity.CLASSLOADING_REASON;
    public static final MethodHandle PACKAGE_LOOKUP = ReflectionUtil.unreflectGetter(() -> cpw.mods.cl.ModuleClassLoader.class.getDeclaredField("packageLookup"));;

    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.AFTER);
    }

    @Override
    public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (CLASSLOADING_REASON.equals(reason)) {
            boolean transformed = CommonTransformer.process(classNode);
            if (transformed) {
                // The cache class is in an unnamed module - add reads so the original class can access it
                ReflectionUtil.addReadsAllUnnamed(findModule(classNode.name.replace('/', '.')));
                return ComputeFlags.SIMPLE_REWRITE;
            }
        }
        return ComputeFlags.NO_REWRITE;
    }

    protected Module findModule(String className) {
        if (Thread.currentThread().getContextClassLoader() instanceof ModuleClassLoader moduleClassLoader) {
            try {
                //noinspection unchecked
                var packageLookup = (Map<String, ResolvedModule>) PACKAGE_LOOKUP.invokeExact(moduleClassLoader);
                var index = className.lastIndexOf('.');
                if (index >= 0) {
                    var pname = className.substring(0, index);
                    var resolved = packageLookup.get(pname);
                    return findGameLayer().findModule(resolved.name()).orElse(null);
                }
            } catch (Throwable e) {
                return null;
            }

        }
        return null;
    }

    protected ModuleLayer findGameLayer() {
        var layerManager = Launcher.INSTANCE.findLayerManager().orElse(null);
        if (layerManager != null) {
            return layerManager.getLayer(IModuleLayerManager.Layer.GAME).orElse(null);
        }
        return null;
    }
}
*///? }
