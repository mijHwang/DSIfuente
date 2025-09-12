package ar.edu.utn.dds.k3003.model;


import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

import ar.edu.utn.dds.k3003.clients.FuenteRetrofitClient;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


class CollectionTest {
    private Fachada fachada;
    private FuenteRetrofitClient pdiClient;

    @BeforeEach
    void setUp() {
        fachada = new Fachada(pdiClient);
    }

    // 1. crear y buscar colección
    @Test
    void agregarColeccion() {
        fachada.agregar(new ColeccionDTO("C1","desc"));
        ColeccionDTO dto = fachada.buscarColeccionXId("C1");
        assertEquals("C1", dto.nombre());
        assertEquals("desc", dto.descripcion());
    }

    // 2. colección duplicada
    @Test
    void agregarColeccion_Repetida_LanzaIAE() {
        fachada.agregar(new ColeccionDTO("C1","d1"));
        assertThrows(IllegalArgumentException.class, () ->
                fachada.agregar(new ColeccionDTO("C1","d2"))
        );
    }

    // 3. buscar colección inexistente
    @Test
    void buscarColeccion_Inexistente_LanzaNoSuch() {
        assertThrows(NoSuchElementException.class, () ->
                fachada.buscarColeccionXId("NOPE")
        );
    }

    // 4. agregar hecho
    @Test
    void agregarHecho_ColeccionExiste_RetornaHechoDTO() {
        fachada.agregar(new ColeccionDTO("H1","d"));
        HechoDTO hDto = fachada.agregar(new HechoDTO(null,"H1","T1", List.of(), null, "L", LocalDateTime.now(), "O"));
        assertNotNull(hDto.id());
        assertEquals("H1", hDto.nombreColeccion());

        HechoDTO found = fachada.buscarHechoXId(hDto.id());
        assertEquals("T1", found.titulo());
    }

    // 5. agregar hecho a colección inexistente
    @Test
    void agregarHecho_ColeccionNoExiste_LanzaIAE() {
        assertThrows(IllegalArgumentException.class, () ->
                fachada.agregar(new HechoDTO(null,"ZZ","T",List.of(), null, "L", LocalDateTime.now(), "O"))
        );
    }

    // 6. buscarHechoXId en múltiples colecciones
    @Test
    void buscarHechoXId_MultipleColecciones_EncuentraCorrecto() {
        fachada.agregar(new ColeccionDTO("C2","d2"));
        HechoDTO h1 = fachada.agregar(new HechoDTO(null,"C2","TT",List.of(), null, "L", LocalDateTime.now(), "O"));
        fachada.agregar(new ColeccionDTO("C3","d3"));
        fachada.agregar(new HechoDTO(null,"C3","UU",List.of(), null, "L", LocalDateTime.now(), "O"));
        // debe encontrar h1
        HechoDTO found = fachada.buscarHechoXId(h1.id());
        assertEquals("C2", found.nombreColeccion());
    }

    // 7. buscarHechoXId inexistente
    @Test
    void buscarHechoXId_Inexistente_LanzaNoSuch() {
        assertThrows(NoSuchElementException.class, () ->
                fachada.buscarHechoXId("no-id")
        );
    }

    // 8. listar hechos sin censura

    @Test
    void buscarHechoXColletionSuccess(){


        fachada.agregar(new ColeccionDTO("C4","d"));
        fachada.agregar(new HechoDTO(null,"C4","Title1"));
        fachada.agregar(new HechoDTO(null,"C4","Title2"));

        // 2) Exercise
        List<HechoDTO> actual = fachada.buscarHechosXColeccion("C4");

        // 3) Verify size
        assertEquals(2, actual.size());

        // 4) Verify IDs and titles only
        List<String> titles = actual.stream()
                .map(HechoDTO::titulo)
                .toList();
        assertTrue(titles.containsAll(List.of("Title1","Title2")));

        List<String> ids = actual.stream()
                .map(HechoDTO::id)
                .toList();
        assertEquals(2, ids.stream().distinct().count(), "Should have two distinct IDs");
    }

    // 9. listar hechos colección vacía
    @Test
    void listarHechos_ColeccionVacia_LanzaNoSuch() {
        fachada.agregar(new ColeccionDTO("C5","d"));
        assertThrows(NoSuchElementException.class, () ->
                fachada.buscarHechosXColeccion("C5")
        );
    }


    // 10. listar hechos colección inexistente
    @Test
    void listarHechos_ColeccionNoExiste_LanzaNoSuch() {
        assertThrows(NoSuchElementException.class, () ->
                fachada.buscarHechosXColeccion("NO")
        );
    }

    // 11. agregarPdI sin procesador
    @Test
    void agregarPdI_SinProcesador_LanzaISE() {
        PdIDTO pdi = new PdIDTO("p1","no",null,null,null,null,List.of());
        assertThrows(IllegalStateException.class, () ->
                fachada.agregar(pdi)
        );
    }


    @Test
    void agregarPdiSuccess_withProxy() {
        fachada.agregar(new ColeccionDTO("C5","d"));
        HechoDTO base = fachada.agregar(new HechoDTO(
                null,
                "C5",
                "T1",
                List.of(),
                null,
                "loc",
                LocalDateTime.now(),
                "orig"
        ));

        // create a proxy for FachadaProcesadorPdI
        FachadaProcesadorPdI fake = (FachadaProcesadorPdI) Proxy.newProxyInstance(
                FachadaProcesadorPdI.class.getClassLoader(),
                new Class[]{FachadaProcesadorPdI.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if (method.getName().equals("procesar")) {
                            PdIDTO in = (PdIDTO) args[0];
                            return new PdIDTO(in.id(), in.hechoId(),
                                    "new stuff", in.lugar(),
                                    in.momento(), in.contenido(),
                                    in.etiquetas());
                        }
                        // default return for void or other return types:
                        return method.getReturnType().isPrimitive() ? 0 : null;
                    }
                }
        );

        fachada.setProcesadorPdI(fake);
        PdIDTO out = fachada.agregar(new PdIDTO("p1", base.id(), null, null, null, null, List.of()));
        assertEquals("new stuff", out.descripcion());
    }




}