
package com.example.my_new_project.service.impl;

import com.example.my_new_project.model.Tattoo;
import com.example.my_new_project.repository.TattooRepository;
import com.example.my_new_project.service.TattooService;
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
public class TattooServiceImpl implements TattooService {

    private final TattooRepository repository;
    private final ImageStorageService imageStorage;

    public TattooServiceImpl(
            TattooRepository repository,
            ImageStorageService imageStorage) {
        this.repository = repository;
        this.imageStorage = imageStorage;
    }

    @Override
    public List<Tattoo> getAllTattoos() {
        return repository.findAllByOrderByDisplayOrderAscIdAsc();
    }

    @Override
    public Tattoo getTattooById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tattoo not found."
                ));
    }

    @Override
    public Tattoo createTattoo(Tattoo tattoo) {
        return repository.save(tattoo);
    }

    @Override
    public Tattoo uploadTattoo(
            MultipartFile file, String name,
            String description, Double price) throws IOException {

        String imageUrl = imageStorage.store(file, "tattoos");

        Tattoo tattoo = new Tattoo(
                name, "Tattoo", description, imageUrl, price
        );

        int nextOrder = repository.findAll().stream()
                .map(Tattoo::getDisplayOrder)
                .filter(value -> value != null)
                .max(Integer::compareTo)
                .orElse(-1) + 1;

        tattoo.setDisplayOrder(nextOrder);
        return repository.save(tattoo);
    }

    @Override
    @Transactional
    public void reorderTattoos(List<Long> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()
                || orderedIds.contains(null)
                || new HashSet<>(orderedIds).size() != orderedIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide unique, non-null tattoo IDs."
            );
        }

        var items = repository.findAllById(orderedIds);

        if (items.size() != orderedIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more tattoo IDs do not exist."
            );
        }

        var byId = new HashMap<Long, Tattoo>();
        items.forEach(item -> byId.put(item.getId(), item));

        for (int i = 0; i < orderedIds.size(); i++) {
            byId.get(orderedIds.get(i)).setDisplayOrder(i);
        }

        repository.saveAll(items);
    }
    @Override
    @Transactional
    public void deleteById(Long id) {
        Tattoo tattoo = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Tattoo not found: " + id));

        try {
            imageStorage.deleteImage(tattoo.getImageUrl());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete tattoo image", e);
        }

        repository.delete(tattoo);
    }
}
