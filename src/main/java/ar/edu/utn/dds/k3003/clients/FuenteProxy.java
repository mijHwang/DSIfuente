package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.app.FachadaProcesadorPdi;
import ar.edu.utn.dds.k3003.dto.PdIDTO;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.logging.Logger;

@Service
public class FuenteProxy implements FachadaProcesadorPdi {

    private static final Logger log = Logger.getLogger(FuenteProxy.class.getName());
    private final RabbitProducer producer;
    private final String queueName;


    public FuenteProxy(RabbitProducer producer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.queueName = System.getenv().getOrDefault("QUEUE_NAME", "hechos");
    }

    @Override
    public PdIDTO procesar(PdIDTO pdi) throws IllegalStateException {
        if (pdi == null) {
            throw new IllegalArgumentException("pdi is null");
        }
        if (pdi.hechoId() == null || pdi.hechoId().isBlank()) {
            throw new IllegalArgumentException("hechoId requerido en PdIDTO");
        }

        log.info("Enqueuing PdIDTO to queue '" + queueName + "'");
        producer.sendMessage(queueName, pdi);

        return pdi;
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
