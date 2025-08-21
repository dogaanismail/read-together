package org.readtogether.chat.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@UtilityClass
public class ChatFileStorageUtils {

    private static final String UPLOAD_DIR = "uploads/chat/";

    public static String uploadFile(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = ChatFileUtils.getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = UUID.randomUUID() + "_" + timestamp + extension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        log.info("File uploaded successfully: {}", uniqueFilename);
        return uniqueFilename;
    }

    public static Path getFilePath(String filename) {

        return Paths.get(UPLOAD_DIR).resolve(filename);
    }
}