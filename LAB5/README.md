# Hệ thống đăng nhập và đăng ký tour đoàn

## 1. Giới thiệu

Dự án này là một ứng dụng Java Web theo mô hình MVC đơn giản, tập trung vào các chức năng:

- Đăng ký tài khoản người dùng
- Đăng nhập hệ thống
- Đăng ký tour đoàn cho khách hàng
- Kết nối cơ sở dữ liệu SQL Server bằng JDBC

Dự án được xây dựng bằng Java Servlet, JSP và mô hình DAO/Model/Controller, phù hợp cho mục đích học tập và làm mẫu cho các hệ thống quản lý khách hàng và đăng ký tour.

---

## 2. Công nghệ sử dụng

- Java 8+
- Java Servlet
- JSP
- SQL Server
- JDBC Driver for SQL Server
- Apache Tomcat
- HTML/CSS/JavaScript cơ bản

---

## 3. Cấu trúc dự án

```text
lab05/
├── config/
│   └── DBContext.java
├── controller/
│   ├── DangNhapServlet.java
│   ├── DangKyServlet.java
│   ├── DangKyDoanServlet.java
│   └── DangXuatServlet.java
├── dao/
│   ├── TaiKhoanDAO.java
│   └── DangKyDoanDAO.java
├── model/
│   ├── TaiKhoan.java
│   └── PhieuDangKyDoan.java
├── WebContent/
│   ├── dangNhap.jsp
│   ├── dangKy.jsp
│   └── dangKyDoan.jsp
├── javax.servlet-api-4.0.1.jar
└── README.md
```

### Mô tả cấu trúc

- `config/`: chứa các lớp cấu hình kết nối đến database
- `controller/`: chứa Servlet xử lý nghiệp vụ
- `dao/`: chứa các lớp thao tác dữ liệu với database
- `model/`: chứa các class đại diện cho dữ liệu
- `WebContent/`: chứa các file JSP giao diện người dùng

---

## 4. Chức năng hiện có

### 4.1 Đăng nhập
- Người dùng nhập tên đăng nhập và mật khẩu
- Hệ thống kiểm tra trong bảng `TAI_KHOAN`
- Nếu đúng, lưu tài khoản vào session và chuyển hướng đến trang đăng ký tour
- Nếu sai, hiển thị thông báo lỗi

### 4.2 Đăng ký tài khoản
- Người dùng nhập thông tin:
  - Tên đăng nhập
  - Mật khẩu
  - Họ tên
  - Email
  - Số điện thoại
- Dữ liệu được lưu vào bảng `TAI_KHOAN`
- Mặc định vai trò được gán là `KhachHang`

### 4.3 Đăng ký tour đoàn
- Form gồm các trường:
  - Mã tour
  - Tên cơ quan/đoàn
  - Địa chỉ
  - Điện thoại
  - Người đại diện
  - Số người
  - Ngày đi dự kiến
  - Địa điểm đón
  - Tiền cọc

---

## 5. Cơ sở dữ liệu

### 5.1 Database
Dự án đang sử dụng database:

```sql
QuanLyTourWeb
```

### 5.2 Kết nối
Tệp `config/DBContext.java` hiện đang dùng chuỗi kết nối SQL Server như sau:

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyTourWeb;encrypt=false;";
private static final String USER = "sa";
private static final String PASS = "your_password";
```

Bạn cần thay đổi `USER` và `PASS` cho đúng với môi trường SQL Server của mình.

### 5.3 Cấu trúc bảng tài khoản cần có

```sql
CREATE TABLE TAI_KHOAN (
    TenDangNhap NVARCHAR(50) PRIMARY KEY,
    MatKhau NVARCHAR(255) NOT NULL,
    HoTen NVARCHAR(100),
    Email NVARCHAR(100),
    DienThoai NVARCHAR(20),
    VaiTro NVARCHAR(50)
);
```

### 5.4 Gợi ý bảng đăng ký tour đoàn
Hiện tại class `PhieuDangKyDoan` đã được định nghĩa nhưng logic xử lý lưu dữ liệu trong `DangKyDoanServlet` và `DangKyDoanDAO` chưa được hoàn thiện. Nếu muốn tích hợp đầy đủ, bạn có thể tạo bảng tương tự:

```sql
CREATE TABLE PHIEU_DANG_KY_DOAN (
    MaPhieuDoan INT IDENTITY(1,1) PRIMARY KEY,
    MaTour NVARCHAR(50),
    TenCoQuanDoan NVARCHAR(255),
    DiaChi NVARCHAR(255),
    DienThoai NVARCHAR(20),
    NguoiDaiDien NVARCHAR(100),
    SoNguoi INT,
    NgayDiChon DATE,
    DiaDiemDon NVARCHAR(255),
    TienCoc DECIMAL(18,0)
);
```

---

## 6. Hướng dẫn chạy dự án

### 6.1 Yêu cầu môi trường

- JDK 8 hoặc cao hơn
- Apache Tomcat 8/9
- SQL Server
- IDE: Eclipse / IntelliJ IDEA / NetBeans
- JDBC Driver cho SQL Server

### 6.2 Bước 1: Tạo database
Tạo database `QuanLyTourWeb` trên SQL Server và tạo bảng `TAI_KHOAN` theo cấu trúc phía trên.

### 6.3 Bước 2: Cập nhật kết nối database
Mở file:

```text
config/DBContext.java
```

Thay thông tin username/password của SQL Server phù hợp với máy bạn.

### 6.4 Bước 3: Thêm driver JDBC
Đảm bảo thư viện JDBC cho SQL Server đã được thêm vào classpath hoặc project libraries.

### 6.5 Bước 4: Chạy trên Tomcat
- Import project vào IDE
- Cấu hình Tomcat
- Deploy project lên server
- Truy cập URL:

```text
http://localhost:8080/<TênProject>/dangNhap.jsp
```

Ví dụ:

```text
http://localhost:8080/lab05/dangNhap.jsp
```

---

## 7. Luồng hoạt động người dùng

### 7.1 Khách hàng đăng nhập
1. Mở trang `dangNhap.jsp`
2. Nhập username và password
3. Gửi request đến `DangNhapServlet`
4. Servlet gọi `TaiKhoanDAO.checkLogin()`
5. Nếu hợp lệ, lưu vào session và chuyển đến `dangKyDoan.jsp`

### 7.2 Khách hàng đăng ký
1. Mở trang `dangKy.jsp`
2. Nhập thông tin tài khoản
3. Gửi request đến `DangKyServlet`
4. Servlet gọi `TaiKhoanDAO.register()`
5. Nếu thành công, chuyển về trang đăng nhập

### 7.3 Đăng ký tour đoàn
1. Sau khi đăng nhập, người dùng truy cập form `dangKyDoan.jsp`
2. Gửi dữ liệu tới `DangKyDoanServlet`
3. Dữ liệu dự kiến sẽ được xử lý và lưu vào database

---

## 8. Các file quan trọng

### Servlet
- `controller/DangNhapServlet.java`: Xử lý đăng nhập
- `controller/DangKyServlet.java`: Xử lý đăng ký tài khoản
- `controller/DangXuatServlet.java`: Xử lý đăng xuất
- `controller/DangKyDoanServlet.java`: Dự kiến xử lý phiếu đăng ký tour đoàn

### DAO
- `dao/TaiKhoanDAO.java`: thực hiện check login và tạo tài khoản mới
- `dao/DangKyDoanDAO.java`: hiện đang chưa được hoàn thiện

### Model
- `model/TaiKhoan.java`: chứa thông tin người dùng
- `model/PhieuDangKyDoan.java`: chứa thông tin phiếu đăng ký tour đoàn

---

## 9. Ghi chú về tình trạng dự án

Dự án hiện đang ở mức cơ bản và có một số phần chưa hoàn thiện, cụ thể:

- `DangKyDoanServlet.java` đang là class rỗng
- `DangKyDoanDAO.java` hiện chưa có nội dung
- Chưa có xử lý lưu `PhieuDangKyDoan` vào database
- Chưa có kiểm tra tính hợp lệ dữ liệu đầu vào đầy đủ
- Chưa có phân quyền người dùng theo vai trò rõ ràng

Đây là dự án học tập, nên phù hợp để tiếp tục mở rộng theo hướng quản lý đăng ký tour và tài khoản người dùng.

---

## 10. Kế hoạch nâng cấp đề xuất

- Hoàn thiện chức năng lưu phiếu đăng ký tour đoàn
- Thêm validation dữ liệu form
- Mã hóa mật khẩu bằng `BCrypt` hoặc `SHA-256`
- Thêm chức năng quản lý tài khoản, admin, nhân viên
- Tạo giao diện dashboard cho quản trị viên
- Tách logic thành service layer rõ ràng hơn
- Bổ sung chức năng tìm kiếm, lọc và quản lý tour

---

## 11. Tác giả

Dự án này là một bài thực hành Java Web về hệ thống đăng nhập, đăng ký tài khoản và đăng ký tour đoàn theo mô hình MVC.

---

## 12. Kết luận

Đây là một ứng dụng mẫu Java Web đơn giản, dễ hiểu và phù hợp cho việc học tập về:

- Servlet
- JSP
- MVC
- JDBC
- SQL Server
- Xử lý session và đăng nhập

Nếu bạn muốn phát triển tiếp, đây là nền tảng rất tốt để mở rộng thành hệ thống quản lý tour du lịch hoàn chỉnh.
