package com.example.my_new_project.repository;

import com.example.my_new_project.model.GallerySetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GallerySettingRepository
        extends JpaRepository<GallerySetting, String> {
}