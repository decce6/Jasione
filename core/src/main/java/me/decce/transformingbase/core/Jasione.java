package me.decce.transformingbase.core;

import me.decce.transformingbase.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Jasione {
    public static JasioneConfig config;

    public static JasioneConfig getConfig() {
        if (config == null) {
            config = loadConfig();
            cleanup();
        }
        return config;
    }

    private static JasioneConfig loadConfig() {
        config = ConfigLoader.load();
        ConfigLoader.save(config);
        return config;
    }

    private static void cleanup() {
        // Cleanup previously dumped classes
        if (Files.exists(Constants.OUTPUT_PATH)) {
            try (var stream = Files.walk(Constants.OUTPUT_PATH)) {
                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (IOException ignored) {
            }
        }
    }
}
