-- Chay SAU 01_Tao_CSDL.sql. Script mau co the chay nhieu lan khong lap du lieu.
USE QuanLyHoSoBenhAn;
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Khoa WHERE TenKhoa=N'Nội tổng quát')
    INSERT dbo.Khoa(TenKhoa,MoTa) VALUES(N'Nội tổng quát', N'Khám nội khoa'),(N'Ngoại tổng quát',N'Khám ngoại khoa'),(N'Nhi',N'Khám trẻ em');
IF NOT EXISTS(SELECT 1 FROM dbo.BacSi)
    INSERT dbo.BacSi(HoTen, ChuyenMon, SoDienThoai, MaKhoa) VALUES
    (N'Nguyễn Văn Bình',N'Nội khoa','0900000001',(SELECT TOP 1 MaKhoa FROM dbo.Khoa WHERE TenKhoa=N'Nội tổng quát')),
    (N'Lê Thị Minh',N'Ngoại khoa','0900000002',(SELECT TOP 1 MaKhoa FROM dbo.Khoa WHERE TenKhoa=N'Ngoại tổng quát'));
IF NOT EXISTS(SELECT 1 FROM dbo.BenhNhan)
    INSERT dbo.BenhNhan(HoTen,NgaySinh,GioiTinh,DiaChi,DienThoai,Anh3x4) VALUES
    (N'Nguyễn Văn A','1995-04-15',N'Nam',N'TP. Hồ Chí Minh','0911111111',NULL),
    (N'Trần Thị B','2001-09-10',N'Nữ',N'TP. Hồ Chí Minh','0922222222',NULL);
PRINT N'Đã thêm dữ liệu mẫu. Vào giao diện Đăng ký + lập hồ sơ để khởi tạo bệnh án.';
GO
