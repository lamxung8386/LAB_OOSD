<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký Tour Đoàn</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f9; display: flex; justify-content: center; padding: 30px 0; }
        .card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); width: 500px; }
        .form-group { margin-bottom: 12px; }
        label { font-weight: bold; display: block; margin-bottom: 4px; font-size: 14px; }
        input[type="text"], input[type="number"], input[type="date"] { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; font-weight: bold; margin-top: 10px; }
        .btn:hover { background-color: #0056b3; }
    </style>
</head>
<body>
    <div class="card">
        <h2 style="text-align: center; color: #333;">Phiếu Đăng Ký Tour Đoàn</h2>
        <form action="DangKyDoanServlet" method="post">
            <div class="form-group">
                <label>Mã Tour:</label>
                <input type="text" name="maTour" required>
            </div>
            <div class="form-group">
                <label>Tên cơ quan / Đoàn:</label>
                <input type="text" name="tenCoQuanDoan" required>
            </div>
            <div class="form-group">
                <label>Địa chỉ:</label>
                <input type="text" name="diaChi" required>
            </div>
            <div class="form-group">
                <label>Điện thoại:</label>
                <input type="text" name="dienThoai" required>
            </div>
            <div class="form-group">
                <label>Người đại diện:</label>
                <input type="text" name="nguoiDaiDien" required>
            </div>
            <div class="form-group">
                <label>Số người đi:</label>
                <input type="number" name="soNguoi" min="1" required>
            </div>
            <div class="form-group">
                <label>Ngày đi dự kiến:</label>
                <input type="date" name="ngayDiChon" required>
            </div>
            <div class="form-group">
                <label>Địa điểm đón:</label>
                <input type="text" name="diaDiemDon" required>
            </div>
            <div class="form-group">
                <label>Tiền cọc (VNĐ):</label>
                <input type="number" name="tienCoc" min="0" required>
            </div>
            <button type="submit" class="btn">Gửi đăng ký tour</button>
        </form>
    </div>
</body>
</html>