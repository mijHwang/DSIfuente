package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente REST para notificar al módulo de búsqueda sobre cambios.
 * Debe ser usado por el módulo Fuente.
 *
 * Las llamadas son asíncronas para no bloquear la operación principal.
 */
@Component
@Slf4j
public class BusquedaNotificationClient {
    private final RestClient restClient;
    private final boolean enabled;

    public BusquedaNotificationClient() {
        String endpoint = System.getenv().getOrDefault("DDS_BUSQUEDA", "http://localhost:8085");
        this.enabled = !endpoint.isBlank();
        if (enabled) {
            this.restClient = RestClient.builder().baseUrl(endpoint).build();
        } else {
            this.restClient = null;
        }
    }

    @Async
    public void notificarHechoCreado(HechoDTO hecho) {
        if (!enabled) return;
        try {
            restClient.post()
                    .uri("/api/indexacion/hecho")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(hecho)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error notificando hecho: {}", e.getMessage());
        }
    }
}