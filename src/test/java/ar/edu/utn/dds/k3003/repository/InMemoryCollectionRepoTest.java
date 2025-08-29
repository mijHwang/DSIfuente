package ar.edu.utn.dds.k3003.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.utn.dds.k3003.model.Collection;

public class InMemoryCollectionRepoTest {
    private InMemoryCollectionRepo repo;

    private Collection colA;

    @BeforeEach
    void setUp() {
        repo = new InMemoryCollectionRepo();
        colA = new Collection("A","D1", LocalDateTime.now(), LocalDateTime.now(), List.of());
        repo.save(colA);
    }

    @Test
    void save_and_findById() {
        assertTrue(repo.findById("A").isPresent());
        assertEquals(colA, repo.findById("A").get());
    }

    @Test
    void modify_swapsTwoInstances() {
        // crea una instancia "igual" pero nueva
        Collection colB = new Collection("A","D2", colA.getCreationTime(), LocalDateTime.now(), List.of());
        repo.save(colB);
        assertEquals(colB, repo.findById("A").get());
        assertNotEquals(colA, repo.findById("A").get());
    }

    @Test
    void copyClone_isolation() {
        List<Collection> snap = repo.findAll();
        assertEquals(1, snap.size());
        snap.clear();
        // la lista interna de repo sigue intacta
        assertTrue(repo.findById("A").isPresent());
    }
}