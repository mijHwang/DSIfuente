package ar.edu.utn.dds.k3003.DTO;

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

    //no imagen
    public PdIDTO(String id, String hechoId, String descripcion, String lugar,
                  LocalDateTime momento, String contenido, List<String> etiquetas) {
        this(id, hechoId, descripcion, lugar, momento, contenido, null, null, null, etiquetas, null, null);
    }

    // fuck images and fuck you too.
    public PdIDTO(String id, String hechoId, String descripcion, String lugar,
                  LocalDateTime momento, String contenido, String imagenUrl) {
        this(id, hechoId, descripcion, lugar, momento, contenido, imagenUrl, null, null, null, null, null);
    }
}