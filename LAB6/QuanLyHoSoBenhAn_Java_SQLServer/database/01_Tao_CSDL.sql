-- Chay toan bo file nay trong SQL Server Management Studio (New Query -> Execute).
-- Demo hoc tap: khong su dung du lieu benh nhan thuc.
IF DB_ID(N'QuanLyHoSoBenhAn') IS NULL
    CREATE DATABASE QuanLyHoSoBenhAn;
GO
USE QuanLyHoSoBenhAn;
GO

IF OBJECT_ID(N'dbo.Khoa', N'U') IS NULL
CREATE TABLE dbo.Khoa (
    MaKhoa INT IDENTITY(1,1) PRIMARY KEY,
    TenKhoa NVARCHAR(120) NOT NULL UNIQUE,
    MoTa NVARCHAR(300) NULL
);
GO
IF OBJECT_ID(N'dbo.BacSi', N'U') IS NULL
CREATE TABLE dbo.BacSi (
    MaBS INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(120) NOT NULL,
    ChuyenMon NVARCHAR(150) NULL,
    SoDienThoai VARCHAR(20) NULL,
    MaKhoa INT NOT NULL REFERENCES dbo.Khoa(MaKhoa)
);
GO
IF OBJECT_ID(N'dbo.BenhNhan', N'U') IS NULL
CREATE TABLE dbo.BenhNhan (
    MaBN INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(120) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh NVARCHAR(10) NOT NULL DEFAULT N'Khác' CHECK(GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    DiaChi NVARCHAR(250) NOT NULL,
    DienThoai VARCHAR(20) NULL,
    Anh3x4 NVARCHAR(500) NULL,
    NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO
IF OBJECT_ID(N'dbo.PhieuDangKy', N'U') IS NULL
CREATE TABLE dbo.PhieuDangKy (
    MaDK INT IDENTITY(1,1) PRIMARY KEY,
    MaBN INT NOT NULL UNIQUE REFERENCES dbo.BenhNhan(MaBN),
    NgayDangKy DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    GhiChu NVARCHAR(300) NULL
);
GO
IF OBJECT_ID(N'dbo.DongPhiNam', N'U') IS NULL
CREATE TABLE dbo.DongPhiNam (
    MaPhi INT IDENTITY(1,1) PRIMARY KEY,
    MaBN INT NOT NULL REFERENCES dbo.BenhNhan(MaBN),
    Nam INT NOT NULL CHECK(Nam BETWEEN 2000 AND 2100),
    SoTien DECIMAL(18,2) NOT NULL CHECK(SoTien > 0),
    NgayDong DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    GhiChu NVARCHAR(300) NULL,
    CONSTRAINT UQ_DongPhiNam UNIQUE(MaBN, Nam)
);
GO
IF OBJECT_ID(N'dbo.HoSoBenhAn', N'U') IS NULL
CREATE TABLE dbo.HoSoBenhAn (
    MaHS INT IDENTITY(1,1) PRIMARY KEY,
    MaBN INT NOT NULL UNIQUE REFERENCES dbo.BenhNhan(MaBN),
    MaDK INT NOT NULL UNIQUE REFERENCES dbo.PhieuDangKy(MaDK),
    NgayLap DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    GhiChu NVARCHAR(500) NULL
);
GO
IF OBJECT_ID(N'dbo.LichHen', N'U') IS NULL
CREATE TABLE dbo.LichHen (
    MaHen INT IDENTITY(1,1) PRIMARY KEY,
    MaBN INT NOT NULL REFERENCES dbo.BenhNhan(MaBN),
    MaBS INT NULL REFERENCES dbo.BacSi(MaBS),
    NgayGioHen DATETIME2 NOT NULL,
    LoaiKham NVARCHAR(30) NOT NULL CHECK(LoaiKham IN(N'Khám bệnh',N'Định kỳ quý')),
    TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Đã đặt' CHECK(TrangThai IN(N'Đã đặt',N'Đã khám',N'Đã hủy')),
    GhiChu NVARCHAR(300) NULL
);
GO
IF OBJECT_ID(N'dbo.PhieuKham', N'U') IS NULL
CREATE TABLE dbo.PhieuKham (
    MaPK INT IDENTITY(1,1) PRIMARY KEY,
    MaHS INT NOT NULL REFERENCES dbo.HoSoBenhAn(MaHS),
    MaBS INT NOT NULL REFERENCES dbo.BacSi(MaBS),
    MaKhoa INT NOT NULL REFERENCES dbo.Khoa(MaKhoa),
    NgayGioKham DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    TrieuChung NVARCHAR(1000) NOT NULL,
    ChanDoan NVARCHAR(1000) NULL,
    LoaiKham NVARCHAR(30) NOT NULL DEFAULT N'Khám bệnh' CHECK(LoaiKham IN(N'Khám bệnh',N'Định kỳ quý')),
    TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Đang khám' CHECK(TrangThai IN(N'Đang khám',N'Chờ thanh toán',N'Đã lưu')),
    NgayLuuHoSo DATETIME2 NULL
);
GO
IF OBJECT_ID(N'dbo.XetNghiem', N'U') IS NULL
CREATE TABLE dbo.XetNghiem (
    MaXN INT IDENTITY(1,1) PRIMARY KEY,
    MaPK INT NOT NULL REFERENCES dbo.PhieuKham(MaPK),
    TenXetNghiem NVARCHAR(200) NOT NULL,
    NgayChiDinh DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    KetQua NVARCHAR(1000) NULL,
    TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Chờ kết quả' CHECK(TrangThai IN(N'Chờ kết quả',N'Đã có kết quả'))
);
GO
IF OBJECT_ID(N'dbo.ToaThuoc', N'U') IS NULL
CREATE TABLE dbo.ToaThuoc (
    MaToa INT IDENTITY(1,1) PRIMARY KEY,
    MaPK INT NOT NULL REFERENCES dbo.PhieuKham(MaPK),
    NgayKe DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    LoiDan NVARCHAR(1000) NULL
);
GO
IF OBJECT_ID(N'dbo.ChiTietToaThuoc', N'U') IS NULL
CREATE TABLE dbo.ChiTietToaThuoc (
    MaCT INT IDENTITY(1,1) PRIMARY KEY,
    MaToa INT NOT NULL REFERENCES dbo.ToaThuoc(MaToa),
    TenThuoc NVARCHAR(200) NOT NULL,
    LieuDung NVARCHAR(150) NOT NULL,
    SoLuong INT NOT NULL CHECK(SoLuong > 0),
    CachDung NVARCHAR(500) NULL
);
GO
IF OBJECT_ID(N'dbo.ThanhToanVienPhi', N'U') IS NULL
CREATE TABLE dbo.ThanhToanVienPhi (
    MaTT INT IDENTITY(1,1) PRIMARY KEY,
    MaPK INT NOT NULL UNIQUE REFERENCES dbo.PhieuKham(MaPK),
    SoTien DECIMAL(18,2) NOT NULL CHECK(SoTien >= 0),
    NgayThanhToan DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đã thanh toán' CHECK(TrangThai IN(N'Đã thanh toán',N'Miễn phí')),
    GhiChu NVARCHAR(300) NULL
);
GO
IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
CREATE TABLE dbo.TaiKhoan (
    MaTK INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap VARCHAR(60) NOT NULL UNIQUE,
    MatKhauHash VARCHAR(150) NOT NULL,
    Muoi VARCHAR(100) NOT NULL,
    VaiTro VARCHAR(15) NOT NULL CHECK(VaiTro IN('ADMIN', 'BACSI', 'TIEPNHAN', 'CHAMSOC')),
    HoTen NVARCHAR(120) NOT NULL,
    KichHoat BIT NOT NULL DEFAULT 1
);
GO
CREATE OR ALTER VIEW dbo.vw_HoSoTongHop AS
SELECT hs.MaHS, bn.MaBN, bn.HoTen AS BenhNhan, bn.NgaySinh, bn.DienThoai,
       hs.NgayLap, COUNT(pk.MaPK) AS SoLanKham, MAX(pk.NgayGioKham) AS LanKhamCuoi
FROM dbo.HoSoBenhAn hs
JOIN dbo.BenhNhan bn ON bn.MaBN = hs.MaBN
LEFT JOIN dbo.PhieuKham pk ON pk.MaHS = hs.MaHS
GROUP BY hs.MaHS, bn.MaBN, bn.HoTen, bn.NgaySinh, bn.DienThoai, hs.NgayLap;
GO
CREATE OR ALTER VIEW dbo.vw_ThongKe AS
SELECT
  (SELECT COUNT(*) FROM dbo.BenhNhan) AS SoBenhNhan,
  (SELECT COUNT(*) FROM dbo.HoSoBenhAn) AS SoHoSo,
  (SELECT COUNT(*) FROM dbo.PhieuKham) AS SoPhieuKham,
  (SELECT COUNT(*) FROM dbo.LichHen WHERE TrangThai=N'Đã đặt') AS LichHenCho,
  (SELECT COUNT(*) FROM dbo.XetNghiem WHERE TrangThai=N'Chờ kết quả') AS XetNghiemCho,
  (SELECT COUNT(*) FROM dbo.PhieuKham WHERE TrangThai=N'Chờ thanh toán') AS ChoThanhToan;
GO
PRINT N'Hoàn tất tạo CSDL QuanLyHoSoBenhAn';
