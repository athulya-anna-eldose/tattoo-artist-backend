package com.example.my_new_project.controller;

import com.example.my_new_project.model.GallerySetting;
import com.example.my_new_project.repository.GallerySettingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    // Fixed keys used by the Drawings and Piercing pages (unchanged).
    private static final Set<String> ALLOWED_KEYS =
            Set.of(
                    "drawings-order", "drawings-hidden",
                    "piercings-order", "piercings-hidden",
                    "tattoos-order", "tattoos-hidden");

    // Work page keys: one order key and one hidden key per category, e.g.
    // work-order-minimal-line-works, work-hidden-black-grey
    private static final Pattern WORK_KEY =
            Pattern.compile("^work-(order|hidden)-[a-z0-9-]{1,60}$");

    private static boolean isAllowed(String key) {
        return ALLOWED_KEYS.contains(key)
                || WORK_KEY.matcher(key).matches();
    }

    private final GallerySettingRepository repository;

    public SettingsController(GallerySettingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String key) {
        if (!isAllowed(key)) {
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

        if (!isAllowed(key)) {
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