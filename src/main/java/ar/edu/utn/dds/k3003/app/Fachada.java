package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.FuenteRetrofitClient;

import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.DTO.PdIDTO;
import ar.edu.utn.dds.k3003.model.Collection;
import ar.edu.utn.dds.k3003.model.Fact;
import ar.edu.utn.dds.k3003.repository.CollectionRepo;
import ar.edu.utn.dds.k3003.repository.InMemoryCollectionRepo;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;

@Service
public class Fachada implements FachadaFuente{

  private CollectionRepo collectionRepo;
  public Collection collection;
  private FachadaProcesadorPdi fachadaProcesadorPdI;
  private EntityManager em;

  public Fachada(FuenteRetrofitClient pdiClient) {
    this.collectionRepo = new InMemoryCollectionRepo();
  }




  @Autowired
  public Fachada(CollectionRepo collectionRepo,
                 FachadaProcesadorPdi fachadaProcesadorPdI,
                 EntityManager em) {
    this.collectionRepo = collectionRepo;
    this.fachadaProcesadorPdI = fachadaProcesadorPdI;
    this.em = em;
  }

  @Override // this adds agregar
  public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {


    if (this.collectionRepo.findById(coleccionDTO.nombre()).isPresent()){

      throw  new IllegalArgumentException(coleccionDTO.nombre() + " ya existe");

    }

    val collection = new Collection(coleccionDTO.nombre(), coleccionDTO.descripcion());
    this.collectionRepo.save(collection);
    return new ColeccionDTO(collection.getName(), collection.getDescription());


  }

  @Override
  public ColeccionDTO buscarColeccionXId(String coleccionId) throws NoSuchElementException {
    val coleccionOptional = this.collectionRepo.findById(coleccionId);
    if(coleccionOptional.isEmpty()){
      throw  new NoSuchElementException(coleccionId + " no existe");
    }
    val coleccion = coleccionOptional.get();
    return new ColeccionDTO(coleccion.getName(),coleccion.getDescription());
  }


  @Transactional
  @Override
  public HechoDTO agregar(HechoDTO hechoDTO) {

    val collectionOptional = collectionRepo.findById(hechoDTO.nombreColeccion());

    val hecho = new Fact(
            hechoDTO.nombreColeccion(), hechoDTO.titulo(),
            hechoDTO.etiquetas(), hechoDTO.categoria(), hechoDTO.ubicacion(),
            hechoDTO.fecha(), hechoDTO.origen());

    if (collectionOptional.isPresent()){


      val oldCollection = collectionOptional.get();
      /*List<Fact> newFacts = new ArrayList<>(oldCollection.getFacts());

      newFacts.add(hecho);
      Collection newCollection = new Collection(oldCollection.getName(), oldCollection.getDescription(),oldCollection.getCreationTime(), LocalDateTime.now(), newFacts);*/
      oldCollection.getFacts().add(hecho);

      this.collectionRepo.save(oldCollection);
      em.flush();


    }else{
      throw  new IllegalArgumentException(hechoDTO.nombreColeccion() + " no existe");
    }

    return new HechoDTO(hecho.getId().toString(), hechoDTO.nombreColeccion(),
            hechoDTO.titulo(),hechoDTO.etiquetas(),hechoDTO.categoria(),
            hechoDTO.ubicacion(),hechoDTO.fecha(), hechoDTO.origen() );
  }




  @Override
  public HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException {

    List<Collection> snap = this.collectionRepo.findAll();
    Iterator<Collection> iterator = snap.iterator();


    while(iterator.hasNext()){

      Collection collection = iterator.next();
      val optionalFact = collection.Facts.stream().filter(x -> x.getId().toString().equals(hechoId)).findFirst();
      if(optionalFact.isPresent()){

        val factToSend = optionalFact.get();
        if (!factToSend.isCensurado()){
        return new HechoDTO(factToSend.getId().toString(),factToSend.getNombreColeccion(), factToSend.getTitulo());
        }
        else
        {


          return new HechoDTO(factToSend.getId().toString(),factToSend.getNombreColeccion(), "Censurado");
        }

      }
    }

    throw  new NoSuchElementException(hechoId + " no existe");
  }

  @Override
  public List<HechoDTO> buscarHechosXColeccion(String coleccionId) throws NoSuchElementException {

    val coleccionOptional = this.collectionRepo.findById(coleccionId);
    if(coleccionOptional.isEmpty()){
      throw  new NoSuchElementException(coleccionId + " no existe");
    }

    val collection = coleccionOptional.get();
    List<Fact> listOfFact = collection.Facts;



    List<HechoDTO> listToSend = new ArrayList<>();
    Iterator<Fact> iterator = listOfFact.iterator();
    if (listOfFact.isEmpty())
      return listToSend;

    while(iterator.hasNext()){

      Fact factToSend = iterator.next();

      if(!factToSend.isCensurado()) {
        listToSend.add(new HechoDTO(factToSend.getId().toString(), factToSend.getNombreColeccion(), factToSend.getTitulo()));
      }
      else{
        listToSend.add(new HechoDTO(factToSend.getId().toString(), factToSend.getNombreColeccion(),"Censurado" ));
      }
    }

    return listToSend;
  }

  @Override
  public void setProcesadorPdI(FachadaProcesadorPdi procesador) {

    this.fachadaProcesadorPdI = procesador;
  }

  @Transactional
  @Override
  public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {

    if(fachadaProcesadorPdI == null){

      throw new IllegalStateException("No procesador configurado");
    }

    PdIDTO pdiAux = fachadaProcesadorPdI.procesar(pdIDTO);

    //PdIDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas)
    String hechoABuscar = pdIDTO.hechoId();

    List<Collection> entireRepo = collectionRepo.findAll();
       /* if (entireRepo.isEmpty())
            throw new IllegalStateException("No hay colecciones cargadas");*/

    Iterator<Collection> iterator = entireRepo.iterator();

    while(iterator.hasNext()){

      Collection currentCollection = iterator.next();
      if(currentCollection.itContains(hechoABuscar)){

        val factOptional  = currentCollection.getFactById(hechoABuscar);

        if (factOptional.isPresent()) {
          val factToModify = factOptional.get();
          List<Fact> facts = currentCollection.getFacts();
          int index = facts.indexOf(factToModify);

          List<String> merged = new ArrayList<>();
          if (factToModify.getEtiquetas() != null) merged.addAll(factToModify.getEtiquetas());
          if (pdiAux.etiquetas() != null) merged.addAll(pdiAux.etiquetas());

          factToModify.ModificarFact(
                  factToModify.getNombreColeccion(),
                  factToModify.getDescripcion(),
                  merged,
                  pdiAux.lugar(),
                  pdiAux.momento()
          );

          //String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas
          //need to save the new fact to the collection and save the new Collection to the repo.

          currentCollection.getFacts().set(index, factToModify);

          /*val optionlToMod = collectionRepo.findById(currentCollection.getName());
          if (!optionlToMod.isPresent()){
            throw new NoSuchElementException(currentCollection + "vacio");
          }
          val colToMod = optionlToMod.get();
          collectionRepo.save(colToMod);

*/

          collectionRepo.save(currentCollection);

          return pdiAux;
        }

      }
    }

    throw new NoSuchElementException(hechoABuscar + " no existe");
  }




  public List<ColeccionDTO> colecciones(){


    return this.collectionRepo.findAll().stream()
            .map(coleccion -> new ColeccionDTO(coleccion.getName(), coleccion.getDescription()))
            .toList();
  }
}

