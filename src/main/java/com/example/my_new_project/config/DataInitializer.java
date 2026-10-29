package com.example.my_new_project.config;

import com.example.my_new_project.model.Tattoo;
import com.example.my_new_project.repository.TattooRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(TattooRepository tattooRepository) {

        return args -> {

            if (tattooRepository.count() == 0) {

                List<Tattoo> tattoos = List.of(

                        new Tattoo(
                                "Minimalist Rose",
                                "Minimalist",
                                "A delicate rose tattoo with clean and elegant lines.",
                                "https://images.unsplash.com/photo-1598371839696-5c5bb00bdc28",
                                2500.0
                        ),

                        new Tattoo(
                                "Geometric Wolf",
                                "Geometric",
                                "A bold geometric wolf design combining sharp lines and symmetry.",
                                "https://images.unsplash.com/photo-1542727365-19732a80dcfd",
                                5000.0
                        ),

                        new Tattoo(
                                "Fine Line Butterfly",
                                "Fine Line",
                                "A beautiful fine-line butterfly design with subtle detailing.",
                                "https://images.unsplash.com/photo-1590246814883-57c511c9f6b0",
                                3000.0
                        ),

                        new Tattoo(
                                "Blackwork Dragon",
                                "Blackwork",
                                "A powerful blackwork dragon tattoo with detailed shading.",
                                "https://images.unsplash.com/photo-1611501275019-9b5cda994e8d",
                                7000.0
                        ),

                        new Tattoo(
                                "Mandala Art",
                                "Mandala",
                                "Intricate mandala tattoo inspired by traditional patterns.",
                                "https://images.unsplash.com/photo-1565058379802-bbe93b2f703a",
                                4500.0
                        )

                );

                tattooRepository.saveAll(tattoos);

                System.out.println("Predefined tattoo data inserted successfully.");
            }
        };
    }
}