package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.Collection;
import ar.edu.utn.dds.k3003.model.Fact;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Profile("!test")
public class JpaColeccionRepository implements CollectionRepo {

    private EntityManager em;

    public JpaColeccionRepository(EntityManager em){
        super();
        this.em = em;
    }

    @Override
    public Collection save(Collection collection){
        this.em.persist(collection);
        return collection;
    }

    @Override
    public Optional<Collection> findById(String id){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Collection> cq = cb.createQuery(Collection.class);
        Root<Collection> root = cq.from(Collection.class);
        cq.select(root).where(cb.equal(root.get("id"), id));
        return Optional.ofNullable(this.em.createQuery(cq).getSingleResult());

    }

    @Override
    public List<Collection> findAll(){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Collection> cq = cb.createQuery(Collection.class);
        Root<Collection> root = cq.from(Collection.class);
        cq.select(root);
        return this.em.createQuery(cq).getResultList();
    }



}