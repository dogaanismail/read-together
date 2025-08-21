package org.readtogether.infrastructure.storage.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class FileOperationUtils {

    public static void createDirectoriesIfNotExists(Path path) throws IOException {

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public static void copyInputStreamToFile(
            InputStream inputStream,
            Path targetPath) throws IOException {

        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean deleteFileIfExists(Path filePath) throws IOException {

        return Files.deleteIfExists(filePath);
    }

    public static boolean fileExists(Path filePath) {

        return Files.exists(filePath);
    }

    public static boolean isReadableFile(Path filePath) {

        return Files.exists(filePath) && Files.isReadable(filePath);
    }

    public static long getFileSize(Path filePath) throws IOException {

        return Files.size(filePath);
    }
}
