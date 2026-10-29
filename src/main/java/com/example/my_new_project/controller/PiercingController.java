
package com.example.my_new_project.controller;

import com.example.my_new_project.model.Piercing;
import com.example.my_new_project.service.PiercingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/piercings")

public class PiercingController {

    private final PiercingService service;

    public PiercingController(PiercingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Piercing> getAllPiercings() {
        return service.getAllPiercings();
    }

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Piercing uploadPiercing(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false)
            String description,
            @RequestParam(value = "price", required = false)
            Double price) throws IOException {

        return service.uploadPiercing(file, name, description, price);
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderPiercings(
            @RequestBody List<Long> orderedIds) {

        if (orderedIds == null) {
            return ResponseEntity.badRequest().build();
        }

        service.reorderPiercings(orderedIds);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePiercing(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
