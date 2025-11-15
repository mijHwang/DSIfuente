package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@EqualsAndHashCode(of="id")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fact
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreColeccion;
    private String descripcion;
    private String titulo;
    private List<String> etiquetas;
    private CategoriaHechoEnum categoria;
    private String ubicacion;
    private LocalDateTime fecha;
    private String origen;
    private LocalDateTime fechaModificacion;

    @Enumerated(EnumType.STRING)
    private HechoEstado estado;


    public enum HechoEstado{

        ACTIVO,
        BORRADO,
        INACTIVO,
        CENSURADO
    }


    // New Fact
    public Fact(Long id, String nombreColeccion,String titulo, List<String> etiquetas, CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen) {

        this.id = id;
        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas;
        this.categoria = categoria;
        this.descripcion = null;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.origen = origen;
        this.fechaModificacion = LocalDateTime.now();
        this.estado = HechoEstado.ACTIVO;

    }

    public Fact(String nombreColeccion,String titulo, List<String> etiquetas, CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen) {


        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas;
        this.categoria = categoria;
        this.descripcion = null;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.origen = origen;
        this.fechaModificacion = LocalDateTime.now();
        this.estado = HechoEstado.ACTIVO;

    }



    public void ModificarFact(String nombreColeccion,String descripcion, List<String> etiquetas, String ubicacion, LocalDateTime fecha) {

        this.nombreColeccion = nombreColeccion;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.fechaModificacion = LocalDateTime.now();
    }


    public void cambiarEstado(HechoEstado estado){

        switch (estado){

            case ACTIVO:
                this.estado = HechoEstado.ACTIVO;
                this.fechaModificacion = LocalDateTime.now();
                break;

                case BORRADO:
                    this.estado = HechoEstado.BORRADO;
                    this.fechaModificacion = LocalDateTime.now();
                    break;

                    case INACTIVO:
                        this.estado = HechoEstado.INACTIVO;
                        this.fechaModificacion = LocalDateTime.now();
                        break;

                        case CENSURADO:
                            this.estado = HechoEstado.CENSURADO;
                            this.fechaModificacion = LocalDateTime.now();
                            break;

                        default:
                            throw new IllegalArgumentException("Estado invalido");
        }

    }

    public Boolean isCensurado(){

        if(this.estado == HechoEstado.CENSURADO){
            return true;
        }
        return false;
    }

}
