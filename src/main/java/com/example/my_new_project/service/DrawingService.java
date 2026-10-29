
package com.example.my_new_project.service;

import com.example.my_new_project.model.Drawing;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DrawingService {
    List<Drawing> getAllDrawings();

    Drawing uploadDrawing(
            MultipartFile file, String name,
            String description, Double price
    ) throws IOException;
    void deleteById(Long id);

    void reorderDrawings(List<Long> orderedIds);
}
