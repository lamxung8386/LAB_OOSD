USE QuanLyNhanSuDB;
GO
SELECT COUNT(*) AS SoPhongBan FROM dbo.PhongBan;
SELECT COUNT(*) AS SoNhanVien FROM dbo.NhanVien;
SELECT COUNT(*) AS SoHopDong FROM dbo.HopDong;
SELECT COUNT(*) AS SoBangLuong FROM dbo.BangLuong;
SELECT ma, hoTen, phongBan, trangThai FROM dbo.NhanVien ORDER BY ma;
SELECT OBJECT_NAME(parent_object_id) AS TenBang, name AS TenKhoaNgoai FROM sys.foreign_keys ORDER BY OBJECT_NAME(parent_object_id);
GO
