package com.prj301.dao;

import com.prj301.entity.UserAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class UserDAO {

    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public UserAccount findByUsername(String username) {
        try {
            TypedQuery<UserAccount> query = em.createQuery(
                "SELECT u FROM UserAccount u WHERE u.username = :username",
                UserAccount.class
            );
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserAccount findByUsernameAndPassword(String username, String password) {
        try {
            TypedQuery<UserAccount> query = em.createQuery(
                "SELECT u FROM UserAccount u WHERE u.username = :username AND u.password = :password",
                UserAccount.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public long countAll() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(u) FROM UserAccount u", Long.class
        );
        return query.getSingleResult();
    }

    public void save(UserAccount user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }
}
