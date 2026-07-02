package me.decce.transformingbase.service.forgelike;

//? if forge || (neoforge && <1.21.9) {
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.util.ReflectionHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransformationServiceImpl implements ITransformationService {
    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public void initialize(IEnvironment environment) {
    //? if forge {
        /*// Required when earlyWindowControl is disabled
        Bootstrapper.bootstrap();
    *///?}
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
        var launchPluginsGetter = ReflectionHelper.unreflectGetter(() -> Launcher.class.getDeclaredField("launchPlugins"));
        var launchPlugins = ReflectionHelper.unchecked(() -> (LaunchPluginHandler) launchPluginsGetter.invoke(Launcher.INSTANCE));
        var pluginsGetter = ReflectionHelper.unreflectGetter(() -> LaunchPluginHandler.class.getDeclaredField("plugins"));
        var plugins = ReflectionHelper.unchecked(() -> (Map<String, ILaunchPluginService>) pluginsGetter.invoke(launchPlugins));
        plugins.put(Constants.MOD_ID, new LaunchPluginServiceImpl());
    }

    //? if forge {
    /*@Override
    public List<ITransformer> transformers() {
        return List.of();
    }
    *///?} else {
    @Override
    public List<? extends ITransformer<?>> transformers() {
        return List.of();
    }
    //?}
}
//?}
