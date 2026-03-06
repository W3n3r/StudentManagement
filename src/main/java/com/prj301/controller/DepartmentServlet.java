package com.prj301.controller;

import com.prj301.dao.DepartmentDAO;
import com.prj301.entity.Department;
import com.prj301.entity.UserAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/departments")
public class DepartmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();

        try {
            DepartmentDAO departmentDAO = new DepartmentDAO(em);
            List<Department> departments = departmentDAO.findAll();
            request.setAttribute("departments", departments);
            request.getRequestDispatcher("/department.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        String action = request.getParameter("action");
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();

        try {
            DepartmentDAO departmentDAO = new DepartmentDAO(em);

            if ("add".equals(action)) {
                String name = request.getParameter("departmentName");
                if (name == null || name.trim().length() < 5 || name.trim().length() > 50) {
                    request.setAttribute("error", "Department name must be between 5 and 50 characters.");
                    request.setAttribute("departments", departmentDAO.findAll());
                    request.getRequestDispatcher("/department.jsp").forward(request, response);
                    return;
                }
                Department dept = new Department(name.trim());
                departmentDAO.save(dept);
                response.sendRedirect(request.getContextPath() + "/departments");

            } else if ("update".equals(action)) {
                String idStr = request.getParameter("id");
                String name = request.getParameter("departmentName");
                if (name == null || name.trim().length() < 5 || name.trim().length() > 50) {
                    request.setAttribute("error", "Department name must be between 5 and 50 characters.");
                    request.setAttribute("departments", departmentDAO.findAll());
                    request.getRequestDispatcher("/department.jsp").forward(request, response);
                    return;
                }
                int id;
                try {
                    id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/departments");
                    return;
                }
                Department dept = departmentDAO.findById(id);
                if (dept != null) {
                    dept.setDepartmentName(name.trim());
                    departmentDAO.update(dept);
                }
                response.sendRedirect(request.getContextPath() + "/departments");

            } else if ("delete".equals(action)) {
                String idStr = request.getParameter("id");
                int id;
                try {
                    id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/departments");
                    return;
                }
                departmentDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/departments");

            } else {
                response.sendRedirect(request.getContextPath() + "/departments");
            }
        } finally {
            em.close();
        }
    }
}
