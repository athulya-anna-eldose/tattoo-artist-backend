
package com.example.my_new_project.service;

import com.example.my_new_project.model.Tattoo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TattooService {
    List<Tattoo> getAllTattoos();

    Tattoo getTattooById(Long id);

    Tattoo createTattoo(Tattoo tattoo);

    Tattoo uploadTattoo(
            MultipartFile file, String name,
            String description, Double price
    ) throws IOException;

    void reorderTattoos(List<Long> orderedIds);
    void deleteById(Long id);

}
