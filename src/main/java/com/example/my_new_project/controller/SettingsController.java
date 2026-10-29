package com.example.my_new_project.controller;

import com.example.my_new_project.model.GallerySetting;
import com.example.my_new_project.repository.GallerySettingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    // Only these keys can be read or written.
    private static final Set<String> ALLOWED_KEYS =
            Set.of("drawings-order", "drawings-hidden");

    private final GallerySettingRepository repository;

    public SettingsController(GallerySettingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String key) {
        if (!ALLOWED_KEYS.contains(key)) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put(
                "value",
                repository.findById(key)
                        .map(GallerySetting::getSettingValue)
                        .orElse(null)
        );

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> put(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {

        if (!ALLOWED_KEYS.contains(key)) {
            return ResponseEntity.badRequest().build();
        }

        String value = body.get("value");

        if (value == null) {
            return ResponseEntity.badRequest().build();
        }

        repository.save(new GallerySetting(key, value));

        return ResponseEntity.noContent().build();
    }
}