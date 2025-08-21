package org.readtogether.infrastructure.storage.utils;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class LocalStorageUtils {

    public static String buildPublicUrl(
            String publicUrlBase,
            String folder,
            String fileName) {

        return publicUrlBase + "/" + folder + "/" + fileName;
    }

    public static Path buildFilePath(
            Path basePath,
            String folder,
            String fileName) {

        return basePath.resolve(folder).resolve(fileName);
    }

    public static Path buildFolderPath(
            Path basePath,
            String folder) {

        return basePath.resolve(folder);
    }

    public static Path extractPathFromUrl(
            String fileUrl,
            String publicUrlBase,
            Path basePath) {

        if (fileUrl.startsWith(publicUrlBase)) {
            String relativePath = fileUrl.substring(publicUrlBase.length() + 1);
            return basePath.resolve(relativePath);
        }
        return basePath.resolve(fileUrl);
    }

    public static String extractRelativePathFromUrl(
            String fileUrl,
            String publicUrlBase) {

        if (fileUrl.startsWith(publicUrlBase)) {
            return fileUrl.substring(publicUrlBase.length() + 1);
        }
        return fileUrl;
    }

    public static boolean isValidLocalUrl(
            String url,
            String publicUrlBase) {

        return url != null && url.startsWith(publicUrlBase);
    }
}
