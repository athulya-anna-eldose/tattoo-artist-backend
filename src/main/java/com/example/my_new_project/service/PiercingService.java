
package com.example.my_new_project.service;

import com.example.my_new_project.model.Piercing;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PiercingService {
    List<Piercing> getAllPiercings();

    Piercing uploadPiercing(
            MultipartFile file, String name,
            String description, Double price
    ) throws IOException;
    void deleteById(Long id);

    void reorderPiercings(List<Long> orderedIds);
}
