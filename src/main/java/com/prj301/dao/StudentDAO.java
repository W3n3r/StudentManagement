package com.prj301.dao;

import com.prj301.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class StudentDAO {

    private final EntityManager em;

    public StudentDAO(EntityManager em) {
        this.em = em;
    }

    public List<Student> findAll(int page, int pageSize) {
        TypedQuery<Student> query = em.createQuery(
            "SELECT s FROM Student s ORDER BY s.id", Student.class
        );
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    public List<Student> findTopByGpa(int limit) {
        TypedQuery<Student> query = em.createQuery(
            "SELECT s FROM Student s ORDER BY s.gpa DESC", Student.class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public long countAll() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(s) FROM Student s", Long.class
        );
        return query.getSingleResult();
    }

    public Student findById(int id) {
        return em.find(Student.class, id);
    }

    public void save(Student student) {
        em.getTransaction().begin();
        em.persist(student);
        em.getTransaction().commit();
    }

    public void update(Student student) {
        em.getTransaction().begin();
        em.merge(student);
        em.getTransaction().commit();
    }

    public void delete(int id) {
        em.getTransaction().begin();
        Student student = em.find(Student.class, id);
        if (student != null) {
            em.remove(student);
        }
        em.getTransaction().commit();
    }
}
