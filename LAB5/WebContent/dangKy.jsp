<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đăng ký Tài khoản</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f0f2f5; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 350px; }
        .form-group { margin-bottom: 12px; }
        label { display: block; margin-bottom: 4px; font-weight: bold; }
        input { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .btn { width: 100%; background: #28a745; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .error { color: red; font-size: 14px; }
    </style>
</head>
<body>
<div class="card">
    <h2>TẠO TÀI KHOẢN MỚI</h2>
    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="DangKyServlet" method="post">
        <div class="form-group">
            <label>Tên đăng nhập:</label>
            <input type="text" name="username" required>
        </div>
        <div class="form-group">
            <label>Mật khẩu:</label>
            <input type="password" name="password" required>
        </div>
        <div class="form-group">
            <label>Họ và Tên:</label>
            <input type="text" name="hoTen" required>
        </div>
        <div class="form-group">
            <label>Email:</label>
            <input type="email" name="email" required>
        </div>
        <div class="form-group">
            <label>Điện thoại:</label>
            <input type="text" name="phone" required>
        </div>
        <button type="submit" class="btn">Đăng ký</button>
    </form>
    <p style="text-align: center; margin-top: 15px;">
        Đã có tài khoản? <a href="dangNhap.jsp">Đăng nhập</a>
    </p>
</div>
</body>
</html>