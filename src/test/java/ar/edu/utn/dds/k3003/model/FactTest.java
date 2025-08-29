package ar.edu.utn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import ar.edu.utn.dds.k3003.model.Fact;
import org.junit.jupiter.api.Test;

public class FactTest {
    @Test
    void equalsHashCode_basedOnId() {
        Fact f1 = new Fact("ID", "C", "Title", List.of(), null, "loc", LocalDateTime.now(), "orig");
        Fact f2 = new Fact("ID", "C", "Other", List.of("x"), null, "loc2", LocalDateTime.now(), "orig2");
        assertEquals(f1, f2);
        assertEquals(f1.hashCode(), f2.hashCode());
    }
}


