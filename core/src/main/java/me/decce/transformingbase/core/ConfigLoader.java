package me.decce.transformingbase.core;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import me.decce.transformingbase.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {
    private static final Path CONFIG_PATH;
    private static final Path CONFIG_FILE;

    static {
        CONFIG_PATH = Paths.get("config");
        CONFIG_FILE = CONFIG_PATH.resolve(Constants.MOD_ID + ".toml");
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH);
            }
        } catch (IOException ignored) {}
    }

    private static CommentedFileConfig makeNightConfig() {
        return CommentedFileConfig.builder(CONFIG_FILE, TomlFormat.instance())
                .preserveInsertionOrder()
                .sync()
                .build();
    }

    public static void save(JasioneConfig config) {
        try (var night = toNightConfig(config)) {
            night.save();
        } catch (Exception e) {
            Jasione.LOGGER.error("Failed to save configuration!", e);
        }
    }

    public static JasioneConfig load() {
        return loadConfig();
    }

    private static JasioneConfig loadConfig() {
        if (CONFIG_FILE.toFile().exists()) {
            try {
                return fromNightConfig();
            } catch (Exception e) {
                Jasione.LOGGER.error("Failed to read configuration!", e);
            }
        }
        return new JasioneConfig();
    }

    private static CommentedFileConfig toNightConfig(JasioneConfig config) {
        var night = makeNightConfig();
        try {
            for (Field field : JasioneConfig.class.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                String key = field.getName();
                if (field.isAnnotationPresent(JasioneConfig.Key.class)) {
                    key = field.getAnnotation(JasioneConfig.Key.class).value();
                }
                night.set(key, field.get(config));
                if (field.isAnnotationPresent(JasioneConfig.Comment.class)) {
                    night.setComment(key, field.getAnnotation(JasioneConfig.Comment.class).value());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return night;
    }

    private static JasioneConfig fromNightConfig() {
        var config = new JasioneConfig();
        try (var night = makeNightConfig()) {
            night.load();
            for (Field field : JasioneConfig.class.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                String key = field.getName();
                if (field.isAnnotationPresent(JasioneConfig.Key.class)) {
                    key = field.getAnnotation(JasioneConfig.Key.class).value();
                }
                if (night.contains(key)) {
                    field.set(config, night.get(key));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return config;
    }
}
