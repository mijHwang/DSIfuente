package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import java.util.NoSuchElementException;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
GET /pdis
GET /pdis/{id}
GET /pdis?hecho={hechoId}
POST /pdis

*/

public class FuenteProxy implements FachadaProcesadorPdI {

    private final String endpoint;
    private final FuenteRetrofitClient service;

    public FuenteProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_VIANDAS", "http://localhost:8081/"); //change this later.

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(FuenteRetrofitClient.class);
    }

    @Override
    public PdIDTO procesar(PdIDTO var1) throws IllegalStateException{
        return null;
    }

    @Override
    public PdIDTO buscarPdIPorId(String var1) throws NoSuchElementException{
        return null;
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String var1) throws NoSuchElementException{

        return null;
    };

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes var1){ };


    public List<PdIDTO> buscarTodos(){
      return null;
    };

}
