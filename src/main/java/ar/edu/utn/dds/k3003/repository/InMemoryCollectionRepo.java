package ar.edu.utn.dds.k3003.repository;
import ar.edu.utn.dds.k3003.model.Collection;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;





@Repository
@Profile("test")
public class InMemoryCollectionRepo implements CollectionRepo {

  private List<Collection> collections;
  //usage of map? or List? Jesus.

  public InMemoryCollectionRepo() {
    this.collections = new ArrayList<>();
  }


  @Override
  public Optional<Collection> findById(String id) {
    return this.collections.stream().filter(x -> x.getName().equals(id)).findFirst();
  }

  @Override
  public Collection save(Collection col) {

    Optional<Collection> existing = findById(col.getName());
    if (existing.isPresent()) {
      // Remove the old one
      collections.remove(existing.get());
    }

    this.collections.add(col);
    col.setModificationTime(LocalDateTime.now());
    return col;
  }


  public List<Collection> findAll() {
    return new ArrayList<>(collections);
  }


}

