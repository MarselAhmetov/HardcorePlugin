package team404.utils;

import team404.HardcorePlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class FileUtils {

    private final static Logger logger = Logger.getLogger(HardcorePlugin.class.getName());

    public static List<String> readAllLines(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            logger.severe("Error reading file: " + file.getAbsolutePath());
            return Collections.emptyList();
        }
    }

    public static File createFile(File dataFolder, String fileName) {
        Path folderPath = Paths.get(dataFolder.getPath());
        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
                logger.info("Directories created: " + folderPath);
            }
            File file = new File(dataFolder, fileName);
            if (!file.exists()) {
                Files.createFile(file.toPath());
            }
            return file;
        } catch (IOException e) {
            logger.severe("Error creating file: " + dataFolder.getAbsolutePath() + fileName);
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(File file, Collection<String> content) {
        try {
            Files.write(file.toPath(), content, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.severe("Error writing to file: " + file.getAbsolutePath());
        }
    }

    public static void appendToFile(File file, String text) {
        try {
            Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.severe("Error writing to file: " + file.getAbsolutePath());
        }
    }
}
