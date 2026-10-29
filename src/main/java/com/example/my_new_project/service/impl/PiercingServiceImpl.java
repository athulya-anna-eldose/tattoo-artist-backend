package com.example.my_new_project.service.impl;

import com.example.my_new_project.model.Piercing;
import com.example.my_new_project.repository.PiercingRepository;
import com.example.my_new_project.service.PiercingService;
import com.example.my_new_project.storage.ImageStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class PiercingServiceImpl implements PiercingService {

    private final PiercingRepository repository;
    private final ImageStorageService imageStorage;

    public PiercingServiceImpl(
            PiercingRepository repository,
            ImageStorageService imageStorage) {
        this.repository = repository;
        this.imageStorage = imageStorage;
    }

    @Override
    public List<Piercing> getAllPiercings() {
        return repository.findAllByOrderByDisplayOrderAscIdAsc();
    }

    @Override
    public Piercing uploadPiercing(
            MultipartFile file, String name,
            String description, Double price) throws IOException {

        String imageUrl = imageStorage.store(file, "piercings");

        Piercing piercing =
                new Piercing(name, description, imageUrl, price);

        int nextOrder = repository.findAll().stream()
                .map(Piercing::getDisplayOrder)
                .filter(value -> value != null)
                .max(Integer::compareTo)
                .orElse(-1) + 1;

        piercing.setDisplayOrder(nextOrder);
        return repository.save(piercing);
    }

    @Override
    @Transactional
    public void reorderPiercings(List<Long> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()
                || orderedIds.contains(null)
                || new HashSet<>(orderedIds).size() != orderedIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide unique, non-null piercing IDs."
            );
        }

        var items = repository.findAllById(orderedIds);

        var byId = new HashMap<Long, Piercing>();
        items.forEach(item -> byId.put(item.getId(), item));

        // IDs deleted on another device are skipped instead of failing.
        int position = 0;
        for (Long id : orderedIds) {
            Piercing piercing = byId.get(id);
            if (piercing != null) {
                piercing.setDisplayOrder(position++);
            }
        }

        repository.saveAll(items);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Deleting something that is already gone is not an error.
        repository.findById(id).ifPresent(piercing -> {
            try {
                imageStorage.deleteImage(piercing.getImageUrl());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete piercing image", e);
            }
            repository.delete(piercing);
        });
    }
}