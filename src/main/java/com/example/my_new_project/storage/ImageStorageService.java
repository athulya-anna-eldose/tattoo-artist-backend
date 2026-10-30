
package com.example.my_new_project.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private final Cloudinary cloudinary;

    public ImageStorageService() {
        String cloudName = System.getenv("CLOUDINARY_CLOUD_NAME");
        String apiKey = System.getenv("CLOUDINARY_API_KEY");
        String apiSecret = System.getenv("CLOUDINARY_API_SECRET");

        if (cloudName == null || cloudName.isBlank()
                || apiKey == null || apiKey.isBlank()
                || apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalStateException(
                    "Cloudinary environment variables are not configured."
            );
        }

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String store(MultipartFile file, String gallery)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Image size must not exceed 10 MB."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG, and WebP images are allowed."
            );
        }

        if (gallery == null
                || !Set.of("tattoos", "drawings", "piercings")
                .contains(gallery)) {
            throw new IllegalArgumentException(
                    "Invalid gallery name."
            );
        }

        String publicId = UUID.randomUUID().toString();

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", gallery,
                            "public_id", publicId,
                            "resource_type", "image"
                    )
            );

            Object secureUrl = result.get("secure_url");

            if (secureUrl == null) {
                throw new IOException(
                        "Cloudinary did not return an image URL."
                );
            }

            return secureUrl.toString();

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(
                    "Failed to upload image to Cloudinary.", e
            );
        }
    }

    public void deleteImage(String imageUrl) throws IOException {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        // Support deletion of old images stored locally.
        if (imageUrl.startsWith("/uploads/")) {
            Path uploadRoot = Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

            String relativePath =
                    imageUrl.substring("/uploads/".length());

            Path imagePath =
                    uploadRoot.resolve(relativePath).normalize();

            if (!imagePath.startsWith(uploadRoot)) {
                throw new IOException("Invalid image path.");
            }

            Files.deleteIfExists(imagePath);
            return;
        }

        // Cloudinary URLs contain the public ID after /upload/.
        try {
            URI uri = URI.create(imageUrl);

            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"res.cloudinary.com".equalsIgnoreCase(
                    uri.getHost())) {
                return;
            }

            String path = uri.getPath();
            String marker = "/upload/";
            int uploadIndex = path.indexOf(marker);

            if (uploadIndex < 0) {
                throw new IOException(
                        "Invalid Cloudinary image URL."
                );
            }

            String publicId = path.substring(
                    uploadIndex + marker.length()
            );

            // Remove a version prefix, if present.
            publicId = publicId.replaceFirst("^v\\d+/", "");

            // Remove the file extension.
            publicId = publicId.replaceFirst("\\.[^.]+$", "");

            publicId = URLDecoder.decode(
                    publicId, StandardCharsets.UTF_8
            );

            if (publicId.isBlank()) {
                throw new IOException(
                        "Could not determine Cloudinary public ID."
                );
            }

            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );

            Object status = result.get("result");

            if (!"ok".equals(status) && !"not found".equals(status)) {
                throw new IOException(
                        "Cloudinary could not delete the image."
                );
            }

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(
                    "Failed to delete image from Cloudinary.", e
            );
        }
    }
}
