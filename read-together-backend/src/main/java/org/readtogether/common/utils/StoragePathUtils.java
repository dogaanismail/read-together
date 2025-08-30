package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class StoragePathUtils {

    public static String generateFileName(
            String originalFilename) {

        String ext = FileUtils.extractFileExtension(originalFilename);
        if (ext.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        return UUID.randomUUID() + "." + ext;
    }

    public static String generateDateFolder(
            String prefix) {

        String date = LocalDate.now().toString();
        return prefix.endsWith("/") ? prefix + date : prefix + "/" + date;
    }
}

