package com.example.stock.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class LockRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public void getLock(String key) {
        entityManager.createQuery("SELECT GET_LOCK(:key, 3000)")
                .setParameter("key", key)
                .getSingleResult();
    }

    public void releaseLock(String key) {
        entityManager.createQuery("SELECT RELEASE_LOCK(:key)")
                .setParameter("key", key)
                .getSingleResult();
    }
}
