
package com.example.my_new_project.repository;

import com.example.my_new_project.model.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawingRepository
        extends JpaRepository<Drawing, Long> {

    List<Drawing> findAllByOrderByDisplayOrderAscIdAsc();
}
