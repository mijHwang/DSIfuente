package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.FuenteRetrofitClient;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.Collection;
import ar.edu.utn.dds.k3003.model.Fact;
import ar.edu.utn.dds.k3003.repository.CollectionRepo;
import ar.edu.utn.dds.k3003.repository.InMemoryCollectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import lombok.val;

@Service
public class Fachada implements FachadaFuente{

  private CollectionRepo collectionRepo;

  @Autowired
  private FachadaProcesadorPdI FachadaProcesadorPdI;


  private AtomicLong hechoCounter = new AtomicLong(0);
  public Collection collection;
    @Autowired
    private FachadaProcesadorPdI fachadaProcesadorPdI;

  //maybe change FuenteRetrofitClient to pdiClient or something.



  public Fachada(FuenteRetrofitClient pdiClient) {
    this.collectionRepo = new InMemoryCollectionRepo();
  }

  @Autowired
  public Fachada(CollectionRepo collectionRepo) {
    this.collectionRepo = collectionRepo;
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

  @Override
  public HechoDTO agregar(HechoDTO hechoDTO) {

    val collectionOptional = collectionRepo.findById(hechoDTO.nombreColeccion());
    long nuevoId = hechoCounter.incrementAndGet();
    if (collectionOptional.isPresent()){
      val hecho = new Fact(String.valueOf(nuevoId),
              hechoDTO.nombreColeccion(), hechoDTO.titulo(),
              hechoDTO.etiquetas(), hechoDTO.categoria(), hechoDTO.ubicacion(),
              hechoDTO.fecha(), hechoDTO.origen());

      val oldCollection = collectionOptional.get();
      List<Fact> newFacts = new ArrayList<>(oldCollection.getFacts());

      newFacts.add(hecho);
      Collection newCollection = new Collection(oldCollection.getName(), oldCollection.getDescription(),oldCollection.getCreationTime(), LocalDateTime.now(), newFacts);


      this.collectionRepo.save(newCollection);


    }else{
      throw  new IllegalArgumentException(hechoDTO.nombreColeccion() + " no existe");
    }

    return new HechoDTO(String.valueOf(nuevoId), hechoDTO.nombreColeccion(),
            hechoDTO.titulo(),hechoDTO.etiquetas(),hechoDTO.categoria(),
            hechoDTO.ubicacion(),hechoDTO.fecha(), hechoDTO.origen() );
  }

  @Override
  public HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException {

    List<Collection> snap = this.collectionRepo.findAll();
    Iterator<Collection> iterator = snap.iterator();


    while(iterator.hasNext()){

      Collection collection = iterator.next();
      val optionalFact = collection.Facts.stream().filter(x -> x.getId().equals(hechoId)).findFirst();
      if(optionalFact.isPresent()){

        val factToSend = optionalFact.get();
        if (!factToSend.isCensurado()){
        return new HechoDTO(factToSend.getId(),factToSend.getNombreColeccion(), factToSend.getTitulo());}

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

      if(!factToSend.isCensurado())
        listToSend.add(new HechoDTO(factToSend.getId(), factToSend.getNombreColeccion(), factToSend.getTitulo()));

    }

    return listToSend;
  }

  @Override
  public void setProcesadorPdI(FachadaProcesadorPdI procesador) {

    this.FachadaProcesadorPdI = procesador;
  }

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


          factToModify.getEtiquetas().addAll(pdiAux.etiquetas());
          factToModify.ModificarFact(factToModify.getNombreColeccion(),
                  factToModify.getDescripcion(),
                  pdiAux.etiquetas(),
                  pdiAux.lugar(),
                  pdiAux.momento());

          //String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas
          //need to save the new fact to the collection and save the new Collection to the repo.

          facts.set(index, factToModify);

          val optionlToMod = collectionRepo.findById(currentCollection.getName());
          if (!optionlToMod.isPresent()){
            throw new NoSuchElementException(currentCollection + "vacio");
          }
          val colToMod = optionlToMod.get();
          collectionRepo.save(colToMod);

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

