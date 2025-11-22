<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        body { 
            font-family: 'Segoe UI', sans-serif; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh; 
            padding: 0; /* Set to 0 so navbar goes full width */
        }

        /* --- NEW NAVBAR STYLES --- */
        .navbar {
            background-color: #ffffff;
            padding: 15px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 15px rgba(0,0,0,0.08);
            margin-bottom: 40px;
            position: sticky;
            top: 0;
            z-index: 1000;
        }

        .navbar h2 {
            margin: 0;
            color: #333;
            font-size: 24px;
            font-weight: 700;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .navbar-right {
            display: flex;
            align-items: center;
            gap: 25px;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 15px;
            color: #555;
            font-weight: 500;
        }

        .role-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            background-color: #eee; /* Default */
            color: #555;
        }
        
        /* Dynamic Role Colors */
        .role-admin { background-color: #e0f7fa; color: #006064; border: 1px solid #b2ebf2; }
        .role-user { background-color: #f3e5f5; color: #7b1fa2; border: 1px solid #e1bee7; }

        .btn-nav {
            text-decoration: none;
            color: #666;
            font-weight: 600;
            font-size: 15px;
            transition: color 0.2s;
        }
        .btn-nav:hover { color: #667eea; }

        .btn-logout {
            text-decoration: none;
            color: #fff;
            background: #ff6b6b;
            padding: 8px 20px;
            border-radius: 25px;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s ease;
            box-shadow: 0 4px 6px rgba(255, 107, 107, 0.2);
        }
        .btn-logout:hover {
            background: #fa5252;
            transform: translateY(-1px);
            box-shadow: 0 6px 12px rgba(255, 107, 107, 0.3);
        }
        /* --- END NAVBAR STYLES --- */

        .container { 
            max-width: 1200px; 
            margin: 0 auto; 
            background: white; 
            border-radius: 12px; 
            padding: 35px; 
            box-shadow: 0 10px 30px rgba(0,0,0,0.05); 
        }
        
        h1 { color: #2d3748; margin-bottom: 25px; font-size: 28px; }
        
        .message { padding: 15px; margin-bottom: 20px; border-radius: 8px; font-weight: 500; }
        .success { background-color: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }
        .error { background-color: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }
        
        /* Buttons */
        .btn { display: inline-block; padding: 10px 20px; text-decoration: none; border-radius: 6px; border: none; cursor: pointer; font-size: 14px; transition: all 0.3s; font-weight: 500; }
        .btn-primary { background: #667eea; color: white; box-shadow: 0 4px 6px rgba(102, 126, 234, 0.25); }
        .btn-primary:hover { background: #5a67d8; transform: translateY(-1px); }
        
        .btn-secondary { background: #edf2f7; color: #4a5568; }
        .btn-secondary:hover { background: #e2e8f0; color: #2d3748; }
        
        .btn-danger { background: #fff5f5; color: #c53030; border: 1px solid #feb2b2; padding: 8px 16px; }
        .btn-danger:hover { background: #c53030; color: white; border-color: #c53030; }
        
        /* Table Styles */
        table { width: 100%; border-collapse: separate; border-spacing: 0; margin-top: 20px; }
        thead { background: #f7fafc; }
        th { padding: 16px; text-align: left; border-bottom: 2px solid #e2e8f0; font-weight: 600; color: #4a5568; text-transform: uppercase; font-size: 13px; letter-spacing: 0.05em; }
        td { padding: 16px; border-bottom: 1px solid #edf2f7; vertical-align: middle; color: #4a5568; }
        tr:last-child td { border-bottom: none; }
        tr:hover { background-color: #f8fafc; }
        
        th a { color: #4a5568; text-decoration: none; display: flex; align-items: center; gap: 5px; transition: color 0.2s; }
        th a:hover { color: #667eea; }
        
        /* Top Bar & Search */
        .top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; flex-wrap: wrap; gap: 15px; }
        .search-form { display: flex; gap: 10px; }
        .search-input { padding: 10px 15px; border: 1px solid #e2e8f0; border-radius: 6px; width: 280px; transition: border-color 0.2s; }
        .search-input:focus { outline: none; border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }
        
        /* Pagination */
        .pagination { display: flex; justify-content: center; gap: 6px; margin-top: 40px; }
        .pagination a, .pagination span { padding: 8px 16px; border: 1px solid #e2e8f0; text-decoration: none; color: #4a5568; border-radius: 6px; font-size: 14px; transition: all 0.2s; background: white; }
        .pagination a:hover { background-color: #f7fafc; border-color: #cbd5e0; }
        .pagination .active { background-color: #667eea; color: white; border-color: #667eea; }
        .pagination .disabled { color: #a0aec0; pointer-events: none; background: #f7fafc; }
        
        .actions { display: flex; gap: 8px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h2>📚 Student Management</h2>
        <div class="navbar-right">
            <div class="user-info">
                <span>Welcome, <strong>${sessionScope.fullName}</strong></span>
                <span class="role-badge role-${sessionScope.role}">
                    ${sessionScope.role}
                </span>
            </div>
            <a href="dashboard" class="btn-nav">Dashboard</a>
            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </div>

    <div class="container">
        <h1>🎓 Student List</h1>
        
        <c:if test="${not empty param.message}"><div class="message success">✅ ${param.message}</div></c:if>
        <c:if test="${not empty param.error}"><div class="message error">❌ ${param.error}</div></c:if>
        
        <div class="top-bar">
            <c:if test="${sessionScope.role eq 'admin'}">
                <a href="student?action=new" class="btn btn-primary">➕ Add New Student</a>
            </c:if>
            <c:if test="${sessionScope.role ne 'admin'}"><div></div></c:if>
            
            <form action="student" method="GET" class="search-form">
                <input type="hidden" name="action" value="list">
                <input type="text" name="keyword" class="search-input" 
                       placeholder="Search by name or code..." value="${param.keyword}">
                <button type="submit" class="btn btn-secondary">🔍 Search</button>
                <c:if test="${not empty param.keyword}">
                    <a href="student?action=list" class="btn btn-secondary">Clear</a>
                </c:if>
            </form>
        </div>
        
        <c:choose>
            <c:when test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <c:set var="newSortOrder" value="${param.sortOrder eq 'ASC' ? 'DESC' : 'ASC'}" />
                            
                            <th><a href="student?action=list&page=1&keyword=${param.keyword}&sortBy=id&sortOrder=${newSortOrder}">ID ${param.sortBy == 'id' ? (param.sortOrder == 'ASC' ? '▲' : '▼') : ''}</a></th>
                            <th><a href="student?action=list&page=1&keyword=${param.keyword}&sortBy=studentCode&sortOrder=${newSortOrder}">Code ${param.sortBy == 'studentCode' ? (param.sortOrder == 'ASC' ? '▲' : '▼') : ''}</a></th>
                            <th><a href="student?action=list&page=1&keyword=${param.keyword}&sortBy=fullName&sortOrder=${newSortOrder}">Name ${param.sortBy == 'fullName' ? (param.sortOrder == 'ASC' ? '▲' : '▼') : ''}</a></th>
                            <th><a href="student?action=list&page=1&keyword=${param.keyword}&sortBy=email&sortOrder=${newSortOrder}">Email ${param.sortBy == 'email' ? (param.sortOrder == 'ASC' ? '▲' : '▼') : ''}</a></th>
                            <th><a href="student?action=list&page=1&keyword=${param.keyword}&sortBy=major&sortOrder=${newSortOrder}">Major ${param.sortBy == 'major' ? (param.sortOrder == 'ASC' ? '▲' : '▼') : ''}</a></th>
                            
                            <c:if test="${sessionScope.role eq 'admin'}">
                                <th>Actions</th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.id}</td>
                                <td><strong>${student.studentCode}</strong></td>
                                <td>${student.fullName}</td>
                                <td>${student.email}</td>
                                <td>${student.major}</td>
                                
                                <c:if test="${sessionScope.role eq 'admin'}">
                                    <td>
                                        <div class="actions">
                                            <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">✏️ Edit</a>
                                            <a href="student?action=delete&id=${student.id}" class="btn btn-danger" onclick="return confirm('Delete student ${student.studentCode}?')">🗑️</a>
                                        </div>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="student?action=list&page=${currentPage - 1}&keyword=${keyword}&sortBy=${sortBy}&sortOrder=${sortOrder}">Previous</a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${currentPage eq i}">
                                <span class="active">${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="student?action=list&page=${i}&keyword=${keyword}&sortBy=${sortBy}&sortOrder=${sortOrder}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="student?action=list&page=${currentPage + 1}&keyword=${keyword}&sortBy=${sortBy}&sortOrder=${sortOrder}">Next</a>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 60px; color: #a0aec0;">
                    <h3>📂 No students found matching "${keyword}"</h3>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>