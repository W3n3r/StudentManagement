<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Management Page</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #f0f2f5; }
        .header {
            background: #27ae60;
            color: white;
            padding: 14px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 { font-size: 20px; }
        .header .nav-links { display: flex; gap: 12px; align-items: center; }
        .btn-back {
            background: #4a90e2;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
        }
        .btn-back:hover { background: #357abd; }
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
        .container { padding: 24px; max-width: 800px; margin: 0 auto; }
        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 24px;
            margin-bottom: 24px;
        }
        .card h2 { margin-bottom: 16px; color: #333; font-size: 18px; }
        .form-row { display: flex; gap: 12px; align-items: flex-end; }
        .form-group { flex: 1; }
        .form-group label { display: block; margin-bottom: 4px; font-size: 13px; color: #555; font-weight: bold; }
        .form-group input {
            width: 100%; padding: 8px 10px; border: 1px solid #ccc;
            border-radius: 4px; font-size: 13px;
        }
        .form-group input:focus { outline: none; border-color: #27ae60; }
        .btn {
            padding: 9px 20px; border: none; border-radius: 4px;
            cursor: pointer; font-size: 14px; color: white; white-space: nowrap;
        }
        .btn-success { background: #27ae60; }
        .btn-success:hover { background: #219a52; }
        .btn-primary { background: #4a90e2; }
        .btn-primary:hover { background: #357abd; }
        .btn-danger { background: #e74c3c; font-size: 12px; padding: 5px 10px; }
        .btn-danger:hover { background: #c0392b; }
        .btn-warning { background: #f39c12; font-size: 12px; padding: 5px 10px; }
        .btn-warning:hover { background: #d68910; }
        .error-msg {
            background: #fde8e8; color: #c0392b; padding: 10px 14px;
            border-radius: 4px; margin-bottom: 16px; font-size: 14px; border-left: 4px solid #c0392b;
        }
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #27ae60; color: white; padding: 10px 12px; text-align: left; }
        td { padding: 9px 12px; border-bottom: 1px solid #eee; vertical-align: middle; }
        tr:hover td { background: #f9f9f9; }
        .btn-group { display: flex; gap: 4px; }
    </style>
    <script>
        function editDeptFromBtn(btn) {
            var id = btn.getAttribute('data-id');
            var name = btn.getAttribute('data-name');
            document.getElementById('editId').value = id;
            document.getElementById('editName').value = name;
            document.getElementById('editForm').style.display = 'block';
            document.getElementById('addForm').style.display = 'none';
            document.getElementById('editForm').scrollIntoView({behavior: 'smooth'});
        }
        function cancelEdit() {
            document.getElementById('editForm').style.display = 'none';
            document.getElementById('addForm').style.display = 'block';
        }
    </script>
</head>
<body>
<div class="header">
    <h1>Department Management Page</h1>
    <div class="nav-links">
        <span>Welcome, <strong>${sessionScope.user.username}</strong></span>
        <a href="${pageContext.request.contextPath}/students" class="btn-back">Student Management</a>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>
</div>

<div class="container">
    <c:if test="${not empty error}">
        <div class="error-msg">${error}</div>
    </c:if>

    <%-- Add Department Form --%>
    <div class="card" id="addForm">
        <h2>Add Department</h2>
        <form action="${pageContext.request.contextPath}/departments" method="post">
            <input type="hidden" name="action" value="add"/>
            <div class="form-row">
                <div class="form-group">
                    <label>Department Name (5-50 chars)</label>
                    <input type="text" name="departmentName" required minlength="5" maxlength="50"
                           placeholder="e.g. Information Technology"/>
                </div>
                <button type="submit" class="btn btn-success">Add</button>
            </div>
        </form>
    </div>

    <%-- Edit Department Form --%>
    <div class="card" id="editForm" style="display:none;">
        <h2>Update Department</h2>
        <form action="${pageContext.request.contextPath}/departments" method="post">
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="id" id="editId"/>
            <div class="form-row">
                <div class="form-group">
                    <label>Department Name (5-50 chars)</label>
                    <input type="text" name="departmentName" id="editName" required minlength="5" maxlength="50"/>
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
                <button type="button" class="btn btn-danger" onclick="cancelEdit()">Cancel</button>
            </div>
        </form>
    </div>

    <%-- Department List --%>
    <div class="card">
        <h2>Department List</h2>
        <c:choose>
            <c:when test="${empty departments}">
                <p style="color:#888; text-align:center; padding:24px;">No departments found.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Department Name</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="dept" items="${departments}">
                            <tr>
                                <td>${dept.id}</td>
                                <td>${dept.departmentName}</td>
                                <td>
                                    <div class="btn-group">
                                        <button class="btn btn-warning"
                                                data-id="${dept.id}"
                                                data-name="<c:out value='${dept.departmentName}'/>"
                                                onclick="editDeptFromBtn(this)">
                                                    Edit
                                                </button>
                                        <form action="${pageContext.request.contextPath}/departments"
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Delete this department?')">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="id" value="${dept.id}"/>
                                            <button type="submit" class="btn btn-danger">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
