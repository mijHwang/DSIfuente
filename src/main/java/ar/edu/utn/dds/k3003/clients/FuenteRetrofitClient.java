package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface FuenteRetrofitClient {

    @GET("/api/PdIs/{id}")
    Call<PdIDTO> getById(@Path("id") String id);

    @GET("/api/PdIs")
    Call<List<PdIDTO>> listHecho(@Query("hecho") String hecho);

    @POST("/api/PdIs")
    Call<PdIDTO> procesar(@Body PdIDTO pdi);

}
