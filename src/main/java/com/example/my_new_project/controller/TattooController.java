
package com.example.my_new_project.controller;

import com.example.my_new_project.model.Tattoo;
import com.example.my_new_project.service.TattooService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tattoos")
public class TattooController {

    private final TattooService service;

    public TattooController(TattooService service) {
        this.service = service;
    }

    @GetMapping
    public List<Tattoo> getAllTattoos() {
        return service.getAllTattoos();
    }

    @GetMapping("/{id}")
    public Tattoo getTattooById(@PathVariable Long id) {
        return service.getTattooById(id);
    }

    @PostMapping
    public Tattoo createTattoo(@RequestBody Tattoo tattoo) {
        return service.createTattoo(tattoo);
    }

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Tattoo uploadTattoo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false)
            String description,
            @RequestParam(value = "price", required = false)
            Double price) throws IOException {

        return service.uploadTattoo(file, name, description, price);
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderTattoos(
            @RequestBody List<Long> orderedIds) {

        if (orderedIds == null) {
            return ResponseEntity.badRequest().build();
        }

        service.reorderTattoos(orderedIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTattoo(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
