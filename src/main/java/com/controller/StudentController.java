package com.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.dao.StudentDAO;
import com.model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        switch (action) {
            case "new": showNewForm(request, response); break;
            case "edit": showEditForm(request, response); break;
            case "delete": deleteStudent(request, response); break;
            default: listStudents(request, response); break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        switch (action) {
            case "insert": insertStudent(request, response); break;
            case "update": updateStudent(request, response); break;
        }
    }
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Search Keyword
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        
        // 2. Pagination
        int page = 1;
        int recordsPerPage = 5;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // 3. Sorting
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        if (sortBy == null) sortBy = "id";
        if (sortOrder == null) sortOrder = "DESC";
        
        int totalRecords = studentDAO.countStudents(keyword);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        int offset = (page - 1) * recordsPerPage;
        
        List<Student> students = studentDAO.getAllStudents(keyword, recordsPerPage, offset, sortBy, sortOrder);
        
        request.setAttribute("students", students);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Student existingStudent = studentDAO.getStudentById(id);
            request.setAttribute("student", existingStudent);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("student?action=list&error=Invalid Student ID");
        }
    }
    
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        // Validation
        String error = validateStudent(studentCode, email);
        if (error != null) {
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect("student?action=new&error=" + encodedError);
            return;
        }
        
        Student newStudent = new Student(studentCode, fullName, email, major);
        if (studentDAO.addStudent(newStudent)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to add student (Duplicate code?)");
        }
    }
    
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        // Validation
        String error = validateStudent(studentCode, email);
        if (error != null) {
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            response.sendRedirect("student?action=edit&id=" + id + "&error=" + encodedError);
            return;
        }
        
        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);
        
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to update student");
        }
    }
    
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }

    private String validateStudent(String code, String email) {
        if (code == null || !code.matches("[A-Z]{2}[0-9]{3,}")) {
            return "Invalid Student Code! Format: 2 Uppercase letters + 3+ Digits (e.g., SV001)";
        }
        if (email != null && !email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$")) {
            return "Invalid Email Format!";
        }
        return null;
    }
}