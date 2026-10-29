
package com.example.my_new_project.service.impl;

import com.example.my_new_project.model.Drawing;
import com.example.my_new_project.repository.DrawingRepository;
import com.example.my_new_project.service.DrawingService;
import com.example.my_new_project.storage.ImageStorageService;
import jakarta.persistence.EntityNotFoundException;
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
public class DrawingServiceImpl implements DrawingService {

    private final DrawingRepository repository;
    private final ImageStorageService imageStorage;

    public DrawingServiceImpl(
            DrawingRepository repository,
            ImageStorageService imageStorage) {
        this.repository = repository;
        this.imageStorage = imageStorage;
    }

    @Override
    public List<Drawing> getAllDrawings() {
        return repository.findAllByOrderByDisplayOrderAscIdAsc();
    }

    @Override
    public Drawing uploadDrawing(
            MultipartFile file, String name,
            String description, Double price) throws IOException {

        String imageUrl = imageStorage.store(file, "drawings");

        Drawing drawing = new Drawing(name, description, imageUrl, price);

        int nextOrder = repository.findAll().stream()
                .map(Drawing::getDisplayOrder)
                .filter(value -> value != null)
                .max(Integer::compareTo)
                .orElse(-1) + 1;

        drawing.setDisplayOrder(nextOrder);
        return repository.save(drawing);
    }

    @Override
    @Transactional
    public void reorderDrawings(List<Long> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()
                || orderedIds.contains(null)
                || new HashSet<>(orderedIds).size() != orderedIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide unique, non-null drawing IDs."
            );
        }

        var items = repository.findAllById(orderedIds);

        if (items.size() != orderedIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more drawing IDs do not exist."
            );
        }

        var byId = new HashMap<Long, Drawing>();
        items.forEach(item -> byId.put(item.getId(), item));

        for (int i = 0; i < orderedIds.size(); i++) {
            byId.get(orderedIds.get(i)).setDisplayOrder(i);
        }

        repository.saveAll(items);
    }
    @Override
    @Transactional
    public void deleteById(Long id) {
        Drawing drawing = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Drawing not found: " + id));

        try {
            imageStorage.deleteImage(drawing.getImageUrl());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete drawing image", e);
        }

        repository.delete(drawing);
    }
}
