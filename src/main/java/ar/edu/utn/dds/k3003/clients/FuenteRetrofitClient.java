package ar.edu.utn.dds.k3003.clients;


import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.*;

import java.util.List;

public interface FuenteRetrofitClient {

    @GET("/api/PdIs/{id}")
    Call<PdIDTO> getById(@Path("id") String id);

    @GET("/api/PdIs")
    Call<List<PdIDTO>> listHecho(@Query("hecho") String hecho);

    @POST("/api/PdIs")
    Call<PdIDTO> procesar(@Body PdIDTO pdi);

    /*public HechoDTO findHechoById(String id) {
        return restClient.get()
                .uri("/api/hecho/{id}", id)
                .retrieve()
                .body(HechoDTO.class);
    }*/


}


/*
@GetMapping
    public ResponseEntity<List<PdIDTO>> listarPdI(@RequestParam(value = "hecho", required = false) String hecho) {
        if (hecho == null) {
            // Si no se proporciona el parámetro "hecho", devuelve todos los PdIDTO
            return ResponseEntity.ok(fachada.buscarTodos());
        } else {
            // Si se proporciona el parámetro "hecho", devuelve los PdIDTO filtrados por "hecho"
            return ResponseEntity.ok(fachada.buscarPorHecho(hecho));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PdIDTO> obtenerPdI(@PathVariable String id) {
        return ResponseEntity.ok(fachada.buscarPdIPorId(id));
    }

    @PostMapping
    public ResponseEntity<PdIDTO> crearPdI(@RequestBody PdIDTO pdIDTO) {
        return ResponseEntity.ok(fachada.procesar(pdIDTO));
    }

*/

