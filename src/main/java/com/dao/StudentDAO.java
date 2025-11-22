package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.model.Student;

public class StudentDAO {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management_mvc";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) AS total FROM students";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 1. Count students for Pagination (supports Search)
    public int countStudents(String keyword) {
        String sql = "SELECT COUNT(*) FROM students WHERE full_name LIKE ? OR student_code LIKE ?";
        if (keyword == null || keyword.trim().isEmpty()) {
            sql = "SELECT COUNT(*) FROM students";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 2. Get Students with Search, Pagination, and Sorting
    public List<Student> getAllStudents(String keyword, int limit, int offset, String sortBy, String sortOrder) {
        List<Student> students = new ArrayList<>();
        
        // Whitelist columns for sorting to prevent SQL Injection
        String orderByClause = "id";
        if (sortBy != null) {
            switch (sortBy) {
                case "studentCode": orderByClause = "student_code"; break;
                case "fullName": orderByClause = "full_name"; break;
                case "email": orderByClause = "email"; break;
                case "major": orderByClause = "major"; break;
                default: orderByClause = "id";
            }
        }
        
        String orderDirection = (sortOrder != null && sortOrder.equalsIgnoreCase("ASC")) ? "ASC" : "DESC";
        
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ? " +
                     "ORDER BY " + orderByClause + " " + orderDirection + " LIMIT ? OFFSET ?";
        
        if (keyword == null || keyword.trim().isEmpty()) {
            sql = "SELECT * FROM students ORDER BY " + orderByClause + " " + orderDirection + " LIMIT ? OFFSET ?";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }
            
            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex, offset);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                students.add(student);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentCode(rs.getString("student_code"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET student_code = ?, full_name = ?, email = ?, major = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            pstmt.setInt(5, student.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}