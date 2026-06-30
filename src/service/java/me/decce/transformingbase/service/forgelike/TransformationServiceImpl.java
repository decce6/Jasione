package me.decce.transformingbase.service.forgelike;

//? if forge || (neoforge && <1.21.9) {
/*import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.Jasione;

import java.lang.reflect.Field;
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
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
        if (!Jasione.getConfig().enabled) {
            return;
        }
        try {
            Field launchPluginsField = Launcher.class.getDeclaredField("launchPlugins");
            launchPluginsField.setAccessible(true);
            var launchPlugins = (LaunchPluginHandler) launchPluginsField.get(Launcher.INSTANCE);
            Field pluginsGetterField = LaunchPluginHandler.class.getDeclaredField("plugins");
            pluginsGetterField.setAccessible(true);
            var plugins = (Map<String, ILaunchPluginService>) pluginsGetterField.get(launchPlugins);
            plugins.put(Constants.MOD_ID, new LaunchPluginServiceImpl());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    //? if forge {
    /^@Override
    public List<ITransformer> transformers() {
        return List.of();
    }
    ^///?} else {
    @Override
    public List<? extends ITransformer<?>> transformers() {
        return List.of();
    }
    //?}
}
*///?}
