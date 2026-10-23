/* BÀI 7 - QUẢN LÝ NHÂN SỰ: SQL SERVER SCHEMA (có thể chạy lại, không DROP dữ liệu) */

IF DB_ID(N'QuanLyNhanSuDB') IS NULL CREATE DATABASE [QuanLyNhanSuDB];

GO

USE [QuanLyNhanSuDB];

GO

SET NOCOUNT ON;

GO

IF OBJECT_ID(N'dbo.PhongBan', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[PhongBan] (
    [ma] NVARCHAR(40) NOT NULL,
    [ten] NVARCHAR(150) NOT NULL,
    [truongPhong] NVARCHAR(150) NOT NULL,
    CONSTRAINT [PK_PhongBan] PRIMARY KEY ([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[NhanVien] (
    [ma] NVARCHAR(40) NOT NULL,
    [hoTen] NVARCHAR(150) NOT NULL,
    [phongBan] NVARCHAR(40) NOT NULL,
    [ngayVao] DATE NOT NULL,
    [sdt] NVARCHAR(60) NOT NULL,
    [trangThai] NVARCHAR(60) NOT NULL,
    CONSTRAINT [PK_NhanVien] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_NhanVien_PhongBan] FOREIGN KEY ([phongBan]) REFERENCES [dbo].[PhongBan]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.QuyetDinhTuyenDung', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[QuyetDinhTuyenDung] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [ngayKy] DATE NOT NULL,
    [batDau] DATE NOT NULL,
    [ketThuc] DATE NULL,
    [dieuKhoan] NVARCHAR(500) NOT NULL,
    [luongThuViec] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_QuyetDinhTuyenDung] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_QuyetDinhTuyenDung_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.DanhGiaThuViec', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[DanhGiaThuViec] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [ngayDanhGia] DATE NOT NULL,
    [ketQua] NVARCHAR(60) NOT NULL,
    [nhanXet] NVARCHAR(500) NOT NULL,
    CONSTRAINT [PK_DanhGiaThuViec] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_DanhGiaThuViec_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.HopDong', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[HopDong] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [loai] NVARCHAR(60) NOT NULL,
    [ngayKy] DATE NOT NULL,
    [batDau] DATE NOT NULL,
    [ketThuc] DATE NULL,
    CONSTRAINT [PK_HopDong] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_HopDong_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.QuyetDinhLuong', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[QuyetDinhLuong] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [hieuLuc] DATE NOT NULL,
    [luongCoBan] DECIMAL(19,2) NOT NULL,
    [phuCap] DECIMAL(19,2) NOT NULL,
    [docHai] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_QuyetDinhLuong] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_QuyetDinhLuong_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.QuyetDinhChucVu', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[QuyetDinhChucVu] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [chucVu] NVARCHAR(150) NOT NULL,
    [hieuLuc] DATE NOT NULL,
    [phuCap] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_QuyetDinhChucVu] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_QuyetDinhChucVu_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.HoSoBaoHiem', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[HoSoBaoHiem] (
    [ma] NVARCHAR(40) NOT NULL,
    [hopDong] NVARCHAR(40) NOT NULL,
    [bhxh] NVARCHAR(60) NOT NULL,
    [bhyt] NVARCHAR(60) NOT NULL,
    [mucDongThang] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_HoSoBaoHiem] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_HoSoBaoHiem_HopDong] FOREIGN KEY ([hopDong]) REFERENCES [dbo].[HopDong]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.KeHoachPhanCong', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[KeHoachPhanCong] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [ngay] DATE NOT NULL,
    [ca] NVARCHAR(60) NOT NULL,
    [soGio] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_KeHoachPhanCong] PRIMARY KEY ([ma]),
    CONSTRAINT [FK_KeHoachPhanCong_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.ChamCong', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[ChamCong] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [thang] INT NOT NULL,
    [nam] INT NOT NULL,
    [ngayCong] DECIMAL(19,2) NOT NULL,
    [gioLam] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_ChamCong] PRIMARY KEY ([ma]),
    CONSTRAINT [UQ_ChamCong] UNIQUE ([nhanVien], [thang], [nam]),
    CONSTRAINT [FK_ChamCong_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.PhieuPhuTroi', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[PhieuPhuTroi] (
    [ma] NVARCHAR(40) NOT NULL,
    [ngay] DATE NOT NULL,
    [hinhThuc] NVARCHAR(60) NOT NULL,
    CONSTRAINT [PK_PhieuPhuTroi] PRIMARY KEY ([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.ChiTietPhuTroi', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[ChiTietPhuTroi] (
    [ma] NVARCHAR(40) NOT NULL,
    [phieu] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [gio] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_ChiTietPhuTroi] PRIMARY KEY ([ma]),
    CONSTRAINT [UQ_ChiTietPhuTroi] UNIQUE ([phieu], [nhanVien]),
    CONSTRAINT [FK_ChiTietPhuTroi_PhieuPhuTroi] FOREIGN KEY ([phieu]) REFERENCES [dbo].[PhieuPhuTroi]([ma]),
    CONSTRAINT [FK_ChiTietPhuTroi_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.QuyDinhThuongPhat', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[QuyDinhThuongPhat] (
    [ma] NVARCHAR(40) NOT NULL,
    [loai] NVARCHAR(60) NOT NULL,
    [doiTuong] NVARCHAR(60) NOT NULL,
    [maDoiTuong] NVARCHAR(60) NOT NULL,
    [lyDo] NVARCHAR(500) NOT NULL,
    [ngay] DATE NOT NULL,
    [hinhThuc] NVARCHAR(60) NOT NULL,
    [trangThai] NVARCHAR(60) NOT NULL,
    [nguoiKy] NVARCHAR(150) NOT NULL,
    [soTien] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_QuyDinhThuongPhat] PRIMARY KEY ([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.PhieuThuChi', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[PhieuThuChi] (
    [ma] NVARCHAR(40) NOT NULL,
    [quyetDinh] NVARCHAR(40) NOT NULL,
    [loai] NVARCHAR(60) NOT NULL,
    [ngay] DATE NOT NULL,
    [ghiChu] NVARCHAR(500) NOT NULL,
    [soTien] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_PhieuThuChi] PRIMARY KEY ([ma]),
    CONSTRAINT [UQ_PhieuThuChi] UNIQUE ([quyetDinh]),
    CONSTRAINT [FK_PhieuThuChi_QuyDinhThuongPhat] FOREIGN KEY ([quyetDinh]) REFERENCES [dbo].[QuyDinhThuongPhat]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.BangLuong', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[BangLuong] (
    [ma] NVARCHAR(40) NOT NULL,
    [nhanVien] NVARCHAR(40) NOT NULL,
    [thang] INT NOT NULL,
    [nam] INT NOT NULL,
    [luongCong] DECIMAL(19,2) NOT NULL,
    [phuCap] DECIMAL(19,2) NOT NULL,
    [tienPhuTroi] DECIMAL(19,2) NOT NULL,
    [thuong] DECIMAL(19,2) NOT NULL,
    [phat] DECIMAL(19,2) NOT NULL,
    [baoHiem] DECIMAL(19,2) NOT NULL,
    [thucLinh] DECIMAL(19,2) NOT NULL,
    CONSTRAINT [PK_BangLuong] PRIMARY KEY ([ma]),
    CONSTRAINT [UQ_BangLuong] UNIQUE ([nhanVien], [thang], [nam]),
    CONSTRAINT [FK_BangLuong_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.NguoiDung', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[NguoiDung] (
    [taiKhoan] NVARCHAR(40) NOT NULL,
    [matKhau] NVARCHAR(256) NOT NULL,
    [vaiTro] NVARCHAR(60) NOT NULL,
    [nhanVien] NVARCHAR(40) NULL,
    CONSTRAINT [PK_NguoiDung] PRIMARY KEY ([taiKhoan]),
    CONSTRAINT [FK_NguoiDung_NhanVien] FOREIGN KEY ([nhanVien]) REFERENCES [dbo].[NhanVien]([ma])
    );
END;
GO

IF OBJECT_ID(N'dbo.HeThong', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HeThong (ID INT NOT NULL CONSTRAINT PK_HeThong PRIMARY KEY, SoThuTu INT NOT NULL);
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.HeThong WHERE ID=1) INSERT INTO dbo.HeThong(ID,SoThuTu) VALUES(1,100);

GO

SELECT name AS TenBang FROM sys.tables WHERE schema_id = SCHEMA_ID('dbo') ORDER BY name;

GO
