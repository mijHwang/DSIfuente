package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.model.Collection;
import ar.edu.utn.dds.k3003.model.Fact;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import ar.edu.utn.dds.k3003.repository.CollectionRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

import java.time.LocalDateTime;

@Configuration
public class dataLoader {

    @Bean
    CommandLineRunner preloadCollections(CollectionRepo collectionRepo) {
        return args -> {
            // Create a collection
            Collection c = new Collection("coleccion1", "Mi primera colección");
            // Add a Fact to it:
            Fact f = new Fact(
                    "1",
                    "coleccion1",
                    "Hecho de prueba",
                    List.of("tag1", "tag2"),
                    CategoriaHechoEnum.ENTRETENIMIENTO,
                    "BsAs",
                    LocalDateTime.now(),
                    "bootstrapped"
            );
            c.getFacts().add(f);
            // Save into your repo
            collectionRepo.save(c);
        };
    }
}