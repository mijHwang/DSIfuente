package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
GET /pdis
GET /pdis/{id}
GET /pdis?hecho={hechoId}
POST /pdis

*/
@Service
public class FuenteProxy implements FachadaProcesadorPdI {

    private final String endpoint;
    private final FuenteRetrofitClient service;

    public FuenteProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("DDS_PDI", "HTTP://localhost:8081"); //change for direction.

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(FuenteRetrofitClient.class);
    }


    //this is the most basic way to do this. I think
    @Override
    public PdIDTO procesar(PdIDTO pdi) throws IllegalStateException{

        if (pdi.hechoId() == null || pdi.hechoId().isBlank())
            throw new IllegalArgumentException("hechoId requerido en PdIDTO");

        Response<PdIDTO> resp = null;
        try {
            resp = service.procesar(pdi).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return resp.body();
    }





    //importan las dos abajo por el momento?
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
