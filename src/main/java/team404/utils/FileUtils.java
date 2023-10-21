package team404.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static List<String> readAllLines(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
            return Collections.emptyList();
        }
    }

    public static File createFile(File dataFolder, String fileName) {
        File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                System.err.println("Error creating file: " + file.getAbsolutePath());
            }
        }
        return file;
    }

    public static void writeToFile(File file, Collection<String> content) {
        try {
            Files.write(file.toPath(), content, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + file.getAbsolutePath());
        }
    }

    public static void appendToFile(File file, String text) {
        try {
            Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error appending to file: " + file.getAbsolutePath());
        }
    }
}
