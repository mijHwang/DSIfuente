package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.Collection;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public interface JpaColeccionRepository extends JpaRepository<Collection, String>, CollectionRepo {
}