package com.prj301.dao;

import com.prj301.entity.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DepartmentDAO {

    private final EntityManager em;

    public DepartmentDAO(EntityManager em) {
        this.em = em;
    }

    public List<Department> findAll() {
        TypedQuery<Department> query = em.createQuery(
            "SELECT d FROM Department d ORDER BY d.id", Department.class
        );
        return query.getResultList();
    }

    public Department findById(int id) {
        return em.find(Department.class, id);
    }

    public long countAll() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(d) FROM Department d", Long.class
        );
        return query.getSingleResult();
    }

    public void save(Department department) {
        em.getTransaction().begin();
        em.persist(department);
        em.getTransaction().commit();
    }

    public void update(Department department) {
        em.getTransaction().begin();
        em.merge(department);
        em.getTransaction().commit();
    }

    public void delete(int id) {
        em.getTransaction().begin();
        Department department = em.find(Department.class, id);
        if (department != null) {
            em.remove(department);
        }
        em.getTransaction().commit();
    }
}
