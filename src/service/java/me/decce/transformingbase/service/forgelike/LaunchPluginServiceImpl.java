//? forge || (neoforge && <1.21.9) {
/*package me.decce.transformingbase.service.forgelike;

import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.Jasione;
import me.decce.transformingbase.service.transform.CommonTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class LaunchPluginServiceImpl implements ILaunchPluginService {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String CLASSLOADING_REASON = ITransformerActivity.CLASSLOADING_REASON;

    @Override
    public String name() {
        return Constants.MOD_ID;
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.AFTER);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (CLASSLOADING_REASON.equals(reason)) {
            return CommonTransformer.process(classNode);
        }
        return false;
    }
}
*///? }
