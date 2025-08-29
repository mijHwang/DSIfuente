package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/colecciones")
public class CollectionController {

    private final FachadaFuente fachadaFuente;

    @Autowired
    public CollectionController(FachadaFuente fachadaFuente) {
        this.fachadaFuente = fachadaFuente;
    }

    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> listar() {
        return ResponseEntity.ok(fachadaFuente.colecciones());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<ColeccionDTO> buscarColeccionPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(fachadaFuente.buscarColeccionXId(nombre));
    }

    @PostMapping
    public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody ColeccionDTO coleccion) {
        return ResponseEntity.ok(fachadaFuente.agregar(coleccion));
    }

    //this gets hechos de una coleccion particular.
    @GetMapping("/{nombre}/hechos")
    public ResponseEntity<List<HechoDTO>> listarHechosColeccion(@PathVariable String nombre) {

        return ResponseEntity.ok(fachadaFuente.buscarHechosXColeccion(nombre));
    }


}
