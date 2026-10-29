
package com.example.my_new_project.repository;

import com.example.my_new_project.model.Piercing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PiercingRepository
        extends JpaRepository<Piercing, Long> {

    List<Piercing> findAllByOrderByDisplayOrderAscIdAsc();
}
