<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management Page</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; }
        .header {
            background: #4a90e2;
            color: white;
            padding: 14px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 { font-size: 20px; }
        .header .user-info { display: flex; align-items: center; gap: 12px; font-size: 14px; }
        .btn-logout {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
        }
        .btn-logout:hover { background: #c0392b; }
        .btn-dept {
            background: #27ae60;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
        }
        .btn-dept:hover { background: #219a52; }
        .container { padding: 24px; max-width: 1200px; margin: 0 auto; }
        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 24px;
            margin-bottom: 24px;
        }
        .card h2 { margin-bottom: 16px; color: #333; font-size: 18px; }
        .form-row { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 12px; }
        .form-group { flex: 1; min-width: 160px; }
        .form-group label { display: block; margin-bottom: 4px; font-size: 13px; color: #555; font-weight: bold; }
        .form-group input, .form-group select {
            width: 100%; padding: 8px 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 13px;
        }
        .form-group input:focus, .form-group select:focus { outline: none; border-color: #4a90e2; }
        .btn {
            padding: 9px 20px; border: none; border-radius: 4px; cursor: pointer;
            font-size: 14px; color: white;
        }
        .btn-primary { background: #4a90e2; }
        .btn-primary:hover { background: #357abd; }
        .btn-success { background: #27ae60; }
        .btn-success:hover { background: #219a52; }
        .btn-danger { background: #e74c3c; font-size: 12px; padding: 5px 10px; }
        .btn-danger:hover { background: #c0392b; }
        .btn-warning { background: #f39c12; font-size: 12px; padding: 5px 10px; }
        .btn-warning:hover { background: #d68910; }
        .error-msg {
            background: #fde8e8; color: #c0392b; padding: 10px 14px;
            border-radius: 4px; margin-bottom: 16px; font-size: 14px; border-left: 4px solid #c0392b;
        }
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #4a90e2; color: white; padding: 10px 12px; text-align: left; }
        td { padding: 9px 12px; border-bottom: 1px solid #eee; vertical-align: middle; }
        tr:hover td { background: #f9f9f9; }
        .pagination { display: flex; gap: 6px; margin-top: 16px; justify-content: center; }
        .page-btn {
            padding: 6px 12px; border: 1px solid #ccc; border-radius: 4px;
            text-decoration: none; color: #333; font-size: 13px; background: white;
        }
        .page-btn.active { background: #4a90e2; color: white; border-color: #4a90e2; }
        .page-btn:hover:not(.active) { background: #f0f0f0; }
        .btn-group { display: flex; gap: 4px; }
    </style>
    <script>
        function editStudentFromBtn(btn) {
            var id = btn.getAttribute('data-id');
            var studentId = btn.getAttribute('data-studentid');
            var name = btn.getAttribute('data-name');
            var gpa = btn.getAttribute('data-gpa');
            var deptId = btn.getAttribute('data-deptid');
            document.getElementById('editId').value = id;
            document.getElementById('editStudentId').value = studentId;
            document.getElementById('editName').value = name;
            document.getElementById('editGpa').value = gpa;
            document.getElementById('editDepartmentId').value = deptId;
            document.getElementById('editForm').style.display = 'block';
            document.getElementById('addForm').style.display = 'none';
            document.getElementById('editForm').scrollIntoView({behavior: 'smooth'});
        }
        function cancelEdit() {
            document.getElementById('editForm').style.display = 'none';
        }
    </script>
</head>
<body>
<div class="header">
    <h1>Student Management Page</h1>
    <div class="user-info">
        <span>Welcome, <strong>${sessionScope.user.username}</strong></span>
        <c:if test="${sessionScope.user.role == 1}">
            <a href="${pageContext.request.contextPath}/departments" class="btn-dept">Manage Departments</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>
</div>

<div class="container">
    <c:if test="${not empty error}">
        <div class="error-msg">${error}</div>
    </c:if>

    <%-- Add Student Form (Staff only) --%>
    <c:if test="${sessionScope.user.role == 2}">
        <div class="card" id="addForm">
            <h2>Add Student</h2>
            <form action="${pageContext.request.contextPath}/students" method="post">
                <input type="hidden" name="action" value="add"/>
                <div class="form-row">
                    <div class="form-group">
                        <label>Student ID</label>
                        <input type="text" name="studentId" required placeholder="e.g. SV001"/>
                    </div>
                    <div class="form-group">
                        <label>Name (5-50 chars)</label>
                        <input type="text" name="name" required minlength="5" maxlength="50"
                               placeholder="Full name"/>
                    </div>
                    <div class="form-group">
                        <label>GPA (0.0 - 10.0)</label>
                        <input type="number" name="gpa" step="0.1" min="0" max="10" required
                               placeholder="e.g. 8.5"/>
                    </div>
                    <div class="form-group">
                        <label>Department</label>
                        <select name="departmentId" required>
                            <option value="">-- Select Department --</option>
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept.id}">${dept.departmentName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-success">Add Student</button>
            </form>
        </div>

        <%-- Edit Student Form (hidden by default) --%>
        <div class="card" id="editForm" style="display:none;">
            <h2>Update Student</h2>
            <form action="${pageContext.request.contextPath}/students" method="post">
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="id" id="editId"/>
                <div class="form-row">
                    <div class="form-group">
                        <label>Student ID</label>
                        <input type="text" id="editStudentId" readonly style="background:#f5f5f5;"/>
                    </div>
                    <div class="form-group">
                        <label>Name (5-50 chars)</label>
                        <input type="text" name="name" id="editName" required minlength="5" maxlength="50"/>
                    </div>
                    <div class="form-group">
                        <label>GPA (0.0 - 10.0)</label>
                        <input type="number" name="gpa" id="editGpa" step="0.1" min="0" max="10" required/>
                    </div>
                    <div class="form-group">
                        <label>Department</label>
                        <select name="departmentId" id="editDepartmentId" required>
                            <option value="">-- Select Department --</option>
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept.id}">${dept.departmentName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Update Student</button>
                <button type="button" class="btn btn-danger" onclick="cancelEdit()">Cancel</button>
            </form>
        </div>
    </c:if>

    <%-- Student List --%>
    <div class="card">
        <h2>
            <c:choose>
                <c:when test="${isManager}">Top 5 Students by GPA</c:when>
                <c:otherwise>Student List</c:otherwise>
            </c:choose>
        </h2>

        <c:choose>
            <c:when test="${empty students}">
                <p style="color:#888; text-align:center; padding:24px;">No students found.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>GPA</th>
                            <th>Department</th>
                            <th>Created By</th>
                            <th>Created At</th>
                            <th>Updated At</th>
                            <c:if test="${sessionScope.user.role == 2}">
                                <th>Actions</th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${students}">
                            <tr>
                                <td>${s.id}</td>
                                <td><c:out value="${s.studentId}"/></td>
                                <td><c:out value="${s.name}"/></td>
                                <td>${s.gpa}</td>
                                <td><c:out value="${s.department.departmentName}"/></td>
                                <td><c:out value="${s.createdBy}"/></td>
                                <td>${s.createdAt}</td>
                                <td>${s.updatedAt}</td>
                                <c:if test="${sessionScope.user.role == 2}">
                                    <td>
                                        <c:if test="${s.createdBy == sessionScope.user.username}">
                                            <div class="btn-group">
                                                <button class="btn btn-warning"
                                                    data-id="${s.id}"
                                                    data-studentid="<c:out value='${s.studentId}'/>"
                                                    data-name="<c:out value='${s.name}'/>"
                                                    data-gpa="${s.gpa}"
                                                    data-deptid="${s.department.id}"
                                                    onclick="editStudentFromBtn(this)">
                                                    Edit
                                                </button>
                                                <form action="${pageContext.request.contextPath}/students"
                                                      method="post" style="display:inline;"
                                                      onsubmit="return confirm('Delete this student?')">
                                                    <input type="hidden" name="action" value="delete"/>
                                                    <input type="hidden" name="id" value="${s.id}"/>
                                                    <button type="submit" class="btn btn-danger">Delete</button>
                                                </form>
                                            </div>
                                        </c:if>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <%-- Pagination (Staff only) --%>
                <c:if test="${not isManager && totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/students?page=${currentPage - 1}"
                               class="page-btn">&laquo; Prev</a>
                        </c:if>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <a href="${pageContext.request.contextPath}/students?page=${i}"
                               class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/students?page=${currentPage + 1}"
                               class="page-btn">Next &raquo;</a>
                        </c:if>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
