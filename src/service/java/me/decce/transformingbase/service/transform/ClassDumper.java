package me.decce.transformingbase.service.transform;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.Jasione;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ClassDumper {
    public static void dump(String internalName, byte[] bytes) {
        try {
            var path = Path.of(Constants.OUTPUT_DIR, internalName + ".class");
            Files.createDirectories(path.getParent());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Jasione.LOGGER.warn("Failed to dump class {}", internalName, e);
        }
    }
}
