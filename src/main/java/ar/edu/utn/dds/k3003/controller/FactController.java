package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.repository.CollectionRepo;
import ar.edu.utn.dds.k3003.model.Collection;
import ar.edu.utn.dds.k3003.model.Fact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/hecho")
public class FactController {

    private final FachadaFuente fachadaFuente;
    private final CollectionRepo collectionRepo;


    @Autowired
    public FactController(FachadaFuente fachadaFuente, CollectionRepo collectionRepo) {
        this.fachadaFuente = fachadaFuente;
        this.collectionRepo = collectionRepo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HechoDTO> buscarHechoPorId(@PathVariable("id") String hechoId) {
        return ResponseEntity.ok(fachadaFuente.buscarHechoXId(hechoId));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchEstado(
            @PathVariable("id") String hechoId,
            @RequestBody Map<String,String> payload
    ) {
        String nuevoEstado = payload.get("estado").toUpperCase();


        for (Collection col : collectionRepo.findAll()) {
            var opt = col.getFactById(hechoId);
            if (opt.isPresent()) {
                Fact fact = opt.get();

                fact.cambiarEstado(Fact.HechoEstado.valueOf(nuevoEstado));

                var original = collectionRepo.findById(col.getName())
                        .orElseThrow(() -> new NoSuchElementException("Colección no existe"));
                collectionRepo.save(col);


                return ResponseEntity.noContent().build();
            }
        }


        throw new NoSuchElementException("Hecho " + hechoId + " no encontrado");
    }

    @PostMapping
    public ResponseEntity<HechoDTO> crearHecho(@RequestBody HechoDTO dto) {
        return ResponseEntity.ok(fachadaFuente.agregar(dto));
    }




}










