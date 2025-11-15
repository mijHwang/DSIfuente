package ar.edu.utn.dds.k3003.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PdIDTO(
        String id,
        String hechoId,
        String descripcion,
        String lugar,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime momento,
        String contenido,
        String imagenUrl,
        String ocrText,
        List<String> etiquetasIA,
        @Deprecated
        List<String> etiquetas,
        String estadoProcesamiento, // changed to String for model-agnostic
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaProcesamiento
) {

}