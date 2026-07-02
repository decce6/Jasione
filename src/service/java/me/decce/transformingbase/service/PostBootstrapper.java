package me.decce.transformingbase.service;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.Jasione;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class PostBootstrapper {
    public static void bootstrap() {
        initConfig();

        // Cleanup previously dumped classes
        if (Files.exists(Constants.OUTPUT_PATH)) {
            try (var stream = Files.walk(Constants.OUTPUT_PATH)) {
                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (IOException ignored) {
            }
        }
    }

    private static void initConfig() {
        Jasione.config = ConfigLoader.load();
        ConfigLoader.save(Jasione.config);
    }
}
