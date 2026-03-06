package com.prj301.controller;

import com.prj301.dao.DepartmentDAO;
import com.prj301.dao.StudentDAO;
import com.prj301.entity.Department;
import com.prj301.entity.Student;
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
import java.time.LocalDate;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private static final int PAGE_SIZE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();

        try {
            StudentDAO studentDAO = new StudentDAO(em);
            DepartmentDAO departmentDAO = new DepartmentDAO(em);

            List<Department> departments = departmentDAO.findAll();
            request.setAttribute("departments", departments);

            if (user.getRole() == 1) {
                // Manager: top 5 highest GPA
                List<Student> students = studentDAO.findTopByGpa(5);
                request.setAttribute("students", students);
                request.setAttribute("isManager", true);
            } else {
                // Staff: all students with pagination
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try {
                        page = Integer.parseInt(pageParam);
                    } catch (NumberFormatException ignored) {}
                }
                if (page < 1) page = 1;

                long totalStudents = studentDAO.countAll();
                int totalPages = (int) Math.ceil((double) totalStudents / PAGE_SIZE);
                if (totalPages < 1) totalPages = 1;
                if (page > totalPages) page = totalPages;

                List<Student> students = studentDAO.findAll(page, PAGE_SIZE);
                request.setAttribute("students", students);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("isManager", false);
            }

            request.getRequestDispatcher("/student.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user.getRole() != 2) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        String action = request.getParameter("action");

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();

        try {
            if ("add".equals(action)) {
                handleAdd(request, response, em, user);
            } else if ("update".equals(action)) {
                handleUpdate(request, response, em, user);
            } else if ("delete".equals(action)) {
                handleDelete(request, response, em, user);
            } else {
                response.sendRedirect(request.getContextPath() + "/students");
            }
        } finally {
            em.close();
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response,
                           EntityManager em, UserAccount user) throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String gpaStr = request.getParameter("gpa");
        String departmentIdStr = request.getParameter("departmentId");

        String error = validateStudentInput(name, gpaStr, departmentIdStr);
        if (error != null) {
            setErrorAndForward(request, response, em, error, user);
            return;
        }

        double gpa = Double.parseDouble(gpaStr);
        int departmentId = Integer.parseInt(departmentIdStr);

        DepartmentDAO departmentDAO = new DepartmentDAO(em);
        Department department = departmentDAO.findById(departmentId);
        if (department == null) {
            setErrorAndForward(request, response, em, "Invalid department selected.", user);
            return;
        }

        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name);
        student.setGpa(gpa);
        student.setDepartment(department);
        student.setCreatedAt(LocalDate.now());
        student.setUpdatedAt(LocalDate.now());
        student.setCreatedBy(user.getUsername());

        StudentDAO studentDAO = new StudentDAO(em);
        studentDAO.save(student);
        response.sendRedirect(request.getContextPath() + "/students");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response,
                              EntityManager em, UserAccount user) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String gpaStr = request.getParameter("gpa");
        String departmentIdStr = request.getParameter("departmentId");

        String error = validateStudentInput(name, gpaStr, departmentIdStr);
        if (error != null) {
            setErrorAndForward(request, response, em, error, user);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            setErrorAndForward(request, response, em, "Invalid student ID.", user);
            return;
        }
        StudentDAO studentDAO = new StudentDAO(em);
        Student student = studentDAO.findById(id);

        if (student == null) {
            setErrorAndForward(request, response, em, "Student not found.", user);
            return;
        }

        if (!student.getCreatedBy().equals(user.getUsername())) {
            setErrorAndForward(request, response, em, "You can only update students you created.", user);
            return;
        }

        double gpa = Double.parseDouble(gpaStr);
        int departmentId = Integer.parseInt(departmentIdStr);

        DepartmentDAO departmentDAO = new DepartmentDAO(em);
        Department department = departmentDAO.findById(departmentId);
        if (department == null) {
            setErrorAndForward(request, response, em, "Invalid department selected.", user);
            return;
        }

        student.setName(name);
        student.setGpa(gpa);
        student.setDepartment(department);
        student.setUpdatedAt(LocalDate.now());

        studentDAO.update(student);
        response.sendRedirect(request.getContextPath() + "/students");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
                              EntityManager em, UserAccount user) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        StudentDAO studentDAO = new StudentDAO(em);
        Student student = studentDAO.findById(id);

        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        if (!student.getCreatedBy().equals(user.getUsername())) {
            setErrorAndForward(request, response, em, "You can only delete students you created.", user);
            return;
        }

        studentDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/students");
    }

    private String validateStudentInput(String name, String gpaStr, String departmentIdStr) {
        if (name == null || name.trim().length() < 5 || name.trim().length() > 50) {
            return "Name must be between 5 and 50 characters.";
        }
        if (gpaStr == null || gpaStr.isEmpty()) {
            return "GPA is required.";
        }
        try {
            double gpa = Double.parseDouble(gpaStr);
            if (gpa < 0.0 || gpa > 10.0) {
                return "GPA must be between 0.0 and 10.0.";
            }
        } catch (NumberFormatException e) {
            return "GPA must be a valid number.";
        }
        if (departmentIdStr == null || departmentIdStr.isEmpty()) {
            return "Department must not be empty.";
        }
        return null;
    }

    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response,
                                    EntityManager em, String error, UserAccount user)
            throws ServletException, IOException {
        request.setAttribute("error", error);

        DepartmentDAO departmentDAO = new DepartmentDAO(em);
        StudentDAO studentDAO = new StudentDAO(em);
        List<Department> departments = departmentDAO.findAll();
        request.setAttribute("departments", departments);

        if (user.getRole() == 1) {
            request.setAttribute("students", studentDAO.findTopByGpa(5));
            request.setAttribute("isManager", true);
        } else {
            long totalStudents = studentDAO.countAll();
            int totalPages = (int) Math.ceil((double) totalStudents / PAGE_SIZE);
            if (totalPages < 1) totalPages = 1;
            request.setAttribute("students", studentDAO.findAll(1, PAGE_SIZE));
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("isManager", false);
        }

        request.getRequestDispatcher("/student.jsp").forward(request, response);
    }
}
