
package com.example.my_new_project.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public String store(MultipartFile file, String gallery)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Image size must not exceed 10 MB."
            );
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG, and WebP images are allowed."
            );
        }

        Path folder = Paths.get("uploads", gallery)
                .toAbsolutePath().normalize();

        Files.createDirectories(folder);

        String extension = switch (file.getContentType()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        String filename = UUID.randomUUID() + extension;
        Path destination = folder.resolve(filename).normalize();

        if (!destination.startsWith(folder)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        Files.copy(file.getInputStream(), destination);

        return "/uploads/" + gallery + "/" + filename;
    }
    public void deleteImage(String imageUrl) throws IOException {
        // Ignore images that aren't stored by our backend.
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) {
            return;
        }

        Path uploadRoot = Paths.get("uploads")
                .toAbsolutePath()
                .normalize();

        String relativePath =
                imageUrl.substring("/uploads/".length());

        Path imagePath = uploadRoot.resolve(relativePath).normalize();

        // Prevent paths from escaping the uploads directory.
        if (!imagePath.startsWith(uploadRoot)) {
            throw new IOException("Invalid image path");
        }

        Files.deleteIfExists(imagePath);
    }
}
