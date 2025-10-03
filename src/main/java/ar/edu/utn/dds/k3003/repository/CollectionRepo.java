package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.Collection;
import java.util.Optional;
import java.util.List;

public interface CollectionRepo {

  Optional<Collection> findById(String id);
  Collection save(Collection col);
  List<Collection> findAll();

}


