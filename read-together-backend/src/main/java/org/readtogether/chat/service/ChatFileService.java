package org.readtogether.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.chat.model.request.ChatMessageSendRequest;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFileService {

    private final ChatService chatService;
    private final ChatWebSocketService chatWebSocketService;

    private static final String UPLOAD_DIR = "uploads/chat/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", // Images
        ".pdf", ".doc", ".docx", ".txt", ".rtf", // Documents
        ".mp3", ".wav", ".ogg", ".m4a", // Audio
        ".mp4", ".avi", ".mkv", ".mov", ".webm", // Video
        ".zip", ".rar", ".7z", ".tar", ".gz" // Archives
    };

    @Transactional
    public ChatMessageResponse sendMessageWithFile(
            ChatMessageSendRequest request,
            UUID senderId) throws IOException {
        
        log.info("Processing message with file attachment for room: {}", request.getChatRoomId());
        
        MultipartFile file = request.getAttachment();
        String attachmentUrl = null;
        String attachmentName = null;
        Long attachmentSize = null;
        String attachmentType = null;

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            
            // Upload file and get URL
            String fileName = uploadFile(file);
            attachmentUrl = "/api/v1/chat/files/" + fileName;
            attachmentName = file.getOriginalFilename();
            attachmentSize = file.getSize();
            attachmentType = file.getContentType();
            
            log.info("Successfully uploaded file: {} for message in room: {}", fileName, request.getChatRoomId());
        }

        // Create message with file attachment info
        ChatMessageResponse message = chatService.sendMessageWithAttachment(
            request, senderId, attachmentUrl, attachmentName, attachmentSize, attachmentType
        );

        // Send real-time notification
        chatWebSocketService.sendMessageToRoom(request.getChatRoomId(), message);

        return message;
    }

    public String getFileUrl(String fileName) {
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        if (Files.exists(filePath)) {
            return filePath.toString();
        }
        throw new IllegalArgumentException("File not found: " + fileName);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is required");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed: " + extension);
        }
    }

    private String uploadFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = UUID.randomUUID().toString() + "_" + timestamp + extension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        log.info("File uploaded successfully: {}", uniqueFilename);
        return uniqueFilename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    public boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    public boolean isVideoFile(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }

    public boolean isAudioFile(String contentType) {
        return contentType != null && contentType.startsWith("audio/");
    }

    public String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}