
package com.example.my_new_project.repository;

import com.example.my_new_project.model.Tattoo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TattooRepository extends JpaRepository<Tattoo, Long> {

    List<Tattoo> findAllByOrderByDisplayOrderAscIdAsc();
}
