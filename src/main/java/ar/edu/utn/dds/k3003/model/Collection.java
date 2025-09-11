package ar.edu.utn.dds.k3003.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;



@Data
@Entity
@AllArgsConstructor
@EqualsAndHashCode( of = {"Name","Description"} )
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Collection {

//conjuntos de hechos organizados bajo un título y descripción, creados y gestionados
//por administradores. Son públicas y no pueden ser editadas ni eliminadas manualmente.


  @Id
  public String Name;

  public String Description;
  public LocalDateTime CreationTime;
  public LocalDateTime ModificationTime;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  /*@JoinColumn(name = "collection_name") */ // This is the foreign key column in Fact table
  //going to erase the above to see what happens.
  public List<Fact> Facts = new ArrayList<>();

  public Collection( String Name, String Description) {

    this.Name = Name;
    this.Description = Description;
    this.CreationTime = LocalDateTime.now();
    this.ModificationTime = LocalDateTime.now();
    this.Facts = new ArrayList<Fact>();
  }


  public Boolean itContains(String hechoID){

    return this.Facts.stream().anyMatch(x -> x.getId().equals(hechoID));
  }

  public Optional<Fact> getFactById(String hechoID){

    return this.Facts.stream().filter(x -> x.getId().equals(hechoID)).findFirst();
  }





}


