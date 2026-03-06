package com.prj301.listener;

import com.prj301.dao.DepartmentDAO;
import com.prj301.dao.UserDAO;
import com.prj301.entity.Department;
import com.prj301.entity.UserAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("StudentManagementPU");
            ServletContext ctx = sce.getServletContext();
            ctx.setAttribute("emf", emf);

            EntityManager em = emf.createEntityManager();
            seedData(em);
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedData(EntityManager em) {
        UserDAO userDAO = new UserDAO(em);

        if (userDAO.countAll() == 0) {
            userDAO.save(new UserAccount("manager", "123456", 1));
            userDAO.save(new UserAccount("staff1", "123456", 2));
            userDAO.save(new UserAccount("staff2", "123456", 2));
            userDAO.save(new UserAccount("guest", "123456", 3));
        }

        DepartmentDAO departmentDAO = new DepartmentDAO(em);
        if (departmentDAO.countAll() == 0) {
            departmentDAO.save(new Department("Information Technology"));
            departmentDAO.save(new Department("Business Administration"));
            departmentDAO.save(new Department("Graphic Design"));
            departmentDAO.save(new Department("Marketing"));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute("emf");
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
