
package com.example.my_new_project.controller;

import com.example.my_new_project.model.Drawing;
import com.example.my_new_project.service.DrawingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/drawings")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000"
})
public class DrawingController {

    private final DrawingService service;

    public DrawingController(DrawingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Drawing> getAllDrawings() {
        return service.getAllDrawings();
    }

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Drawing uploadDrawing(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false)
            String description,
            @RequestParam(value = "price", required = false)
            Double price) throws IOException {

        return service.uploadDrawing(file, name, description, price);
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderDrawings(
            @RequestBody List<Long> orderedIds) {

        if (orderedIds == null) {
            return ResponseEntity.badRequest().build();
        }

        service.reorderDrawings(orderedIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrawing(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
