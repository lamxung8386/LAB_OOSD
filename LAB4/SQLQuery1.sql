/* =========================================================
   DATABASE: e-SHOPPING
   PHẦN: CREATE TABLE + PRIMARY KEY + FOREIGN KEY
   HỆ QUẢN TRỊ: SQL SERVER
   ========================================================= */

-- Nếu database chưa tồn tại thì tạo mới
IF DB_ID('ESHOPPING') IS NULL
BEGIN
    CREATE DATABASE ESHOPPING;
END
GO

USE ESHOPPING;
GO


/* =========================================================
   1. NHÓM SẢN PHẨM
   ========================================================= */
CREATE TABLE NHOMSANPHAM
(
    MaNhom       VARCHAR(10)       NOT NULL,
    TenNhom      NVARCHAR(100)     NOT NULL,

    CONSTRAINT PK_NHOMSANPHAM
        PRIMARY KEY (MaNhom)
);
GO


/* =========================================================
   2. NHÀ SẢN XUẤT
   ========================================================= */
CREATE TABLE NHASANXUAT
(
    MaNSX        VARCHAR(10)       NOT NULL,
    TenNSX       NVARCHAR(100)     NOT NULL,

    CONSTRAINT PK_NHASANXUAT
        PRIMARY KEY (MaNSX)
);
GO


/* =========================================================
   3. SẢN PHẨM
   ========================================================= */
CREATE TABLE SANPHAM
(
    MaSP             VARCHAR(20)       NOT NULL,
    TenSP            NVARCHAR(200)     NOT NULL,
    MaNSX            VARCHAR(10)       NOT NULL,
    MaNhom           VARCHAR(10)       NOT NULL,
    HinhAnh          NVARCHAR(500)     NULL,
    MoTa             NVARCHAR(MAX)     NULL,
    ThongSoKyThuat   NVARCHAR(MAX)     NULL,
    GiaBan           DECIMAL(18,2)     NOT NULL,
    TinhTrang        NVARCHAR(30)      NOT NULL,

    CONSTRAINT PK_SANPHAM
        PRIMARY KEY (MaSP),

    CONSTRAINT FK_SANPHAM_NHASANXUAT
        FOREIGN KEY (MaNSX)
        REFERENCES NHASANXUAT(MaNSX),

    CONSTRAINT FK_SANPHAM_NHOMSANPHAM
        FOREIGN KEY (MaNhom)
        REFERENCES NHOMSANPHAM(MaNhom),

    CONSTRAINT CK_SANPHAM_GIA
        CHECK (GiaBan >= 0),

    CONSTRAINT CK_SANPHAM_TINHTRANG
        CHECK (TinhTrang IN (N'Còn hàng', N'Hết hàng'))
);
GO


/* =========================================================
   4. KHÁCH HÀNG
   ========================================================= */
CREATE TABLE KHACHHANG
(
    MaKH             VARCHAR(20)       NOT NULL,
    HoTen            NVARCHAR(100)     NOT NULL,
    NgaySinh         DATE              NULL,
    CMND_Passport    VARCHAR(30)       NULL,
    DiaChi           NVARCHAR(255)     NULL,
    DienThoai        VARCHAR(20)       NULL,
    TenDangNhap      VARCHAR(50)       NOT NULL,
    MatKhau          VARCHAR(255)      NOT NULL,
    Email            VARCHAR(150)      NULL,

    CONSTRAINT PK_KHACHHANG
        PRIMARY KEY (MaKH),

    CONSTRAINT UQ_KHACHHANG_TENDANGNHAP
        UNIQUE (TenDangNhap),

    CONSTRAINT UQ_KHACHHANG_CMND
        UNIQUE (CMND_Passport),

    CONSTRAINT UQ_KHACHHANG_EMAIL
        UNIQUE (Email)
);
GO


/* =========================================================
   5. GIỎ HÀNG
   ========================================================= */
CREATE TABLE GIOHANG
(
    MaGioHang        VARCHAR(20)       NOT NULL,
    MaKH              VARCHAR(20)       NOT NULL,
    NgayTao           DATETIME         NOT NULL DEFAULT GETDATE(),

    CONSTRAINT PK_GIOHANG
        PRIMARY KEY (MaGioHang),

    CONSTRAINT FK_GIOHANG_KHACHHANG
        FOREIGN KEY (MaKH)
        REFERENCES KHACHHANG(MaKH)
);
GO


/* =========================================================
   6. CHI TIẾT GIỎ HÀNG
   ========================================================= */
CREATE TABLE CT_GIOHANG
(
    MaGioHang        VARCHAR(20)       NOT NULL,
    MaSP             VARCHAR(20)       NOT NULL,
    SoLuong          INT               NOT NULL,

    CONSTRAINT PK_CT_GIOHANG
        PRIMARY KEY (MaGioHang, MaSP),

    CONSTRAINT FK_CT_GIOHANG_GIOHANG
        FOREIGN KEY (MaGioHang)
        REFERENCES GIOHANG(MaGioHang),

    CONSTRAINT FK_CT_GIOHANG_SANPHAM
        FOREIGN KEY (MaSP)
        REFERENCES SANPHAM(MaSP),

    CONSTRAINT CK_CT_GIOHANG_SOLUONG
        CHECK (SoLuong > 0)
);
GO


/* =========================================================
   7. HÌNH THỨC ĐẶT HÀNG / GIAO HÀNG
   ========================================================= */
CREATE TABLE HINHTHUCGIAOHANG
(
    MaHinhThuc       VARCHAR(10)       NOT NULL,
    TenHinhThuc      NVARCHAR(100)     NOT NULL,
    DonGia            DECIMAL(18,2)     NOT NULL,
    ThoiGianXuLy     NVARCHAR(100)     NULL,

    CONSTRAINT PK_HINHTHUCGIAOHANG
        PRIMARY KEY (MaHinhThuc),

    CONSTRAINT CK_HINHTHUCGIAOHANG_DONGIA
        CHECK (DonGia >= 0)
);
GO


/* =========================================================
   8. KHU VỰC GIAO HÀNG
   ========================================================= */
CREATE TABLE KHUVUCGIAOHANG
(
    MaKhuVuc          VARCHAR(10)       NOT NULL,
    TenKhuVuc         NVARCHAR(100)     NOT NULL,

    CONSTRAINT PK_KHUVUCGIAOHANG
        PRIMARY KEY (MaKhuVuc)
);
GO


/* =========================================================
   9. CHI PHÍ GIAO HÀNG
   ========================================================= */
CREATE TABLE CHIPHIGIAOHANG
(
    MaKhuVuc          VARCHAR(10)       NOT NULL,
    MaHinhThuc        VARCHAR(10)       NOT NULL,
    ChiPhi            DECIMAL(18,2)     NOT NULL,

    CONSTRAINT PK_CHIPHIGIAOHANG
        PRIMARY KEY (MaKhuVuc, MaHinhThuc),

    CONSTRAINT FK_CHIPHIGIAOHANG_KHUVUC
        FOREIGN KEY (MaKhuVuc)
        REFERENCES KHUVUCGIAOHANG(MaKhuVuc),

    CONSTRAINT FK_CHIPHIGIAOHANG_HINHTHUC
        FOREIGN KEY (MaHinhThuc)
        REFERENCES HINHTHUCGIAOHANG(MaHinhThuc),

    CONSTRAINT CK_CHIPHIGIAOHANG_CHIPHI
        CHECK (ChiPhi >= 0)
);
GO


/* =========================================================
   10. ĐƠN ĐẶT HÀNG
   ========================================================= */
CREATE TABLE DONDATHANG
(
    MaDonHang            VARCHAR(20)       NOT NULL,
    MaKH                 VARCHAR(20)       NOT NULL,
    MaHinhThuc           VARCHAR(10)       NOT NULL,
    MaKhuVuc             VARCHAR(10)       NOT NULL,

    -- Thông tin người nhận
    TenNguoiNhan         NVARCHAR(100)     NOT NULL,
    DiaChiNguoiNhan      NVARCHAR(255)     NOT NULL,
    DienThoaiNguoiNhan   VARCHAR(20)       NOT NULL,

    -- Thông tin tiền
    TienHang             DECIMAL(18,2)     NOT NULL,
    PhiGiaoHang          DECIMAL(18,2)     NOT NULL DEFAULT 0,
    TongTien             DECIMAL(18,2)     NOT NULL,

    NgayDat              DATETIME          NOT NULL DEFAULT GETDATE(),
    TrangThai            NVARCHAR(50)      NOT NULL DEFAULT N'Chờ xử lý',

    CONSTRAINT PK_DONDATHANG
        PRIMARY KEY (MaDonHang),

    CONSTRAINT FK_DONDATHANG_KHACHHANG
        FOREIGN KEY (MaKH)
        REFERENCES KHACHHANG(MaKH),

    CONSTRAINT FK_DONDATHANG_HINHTHUC
        FOREIGN KEY (MaHinhThuc)
        REFERENCES HINHTHUCGIAOHANG(MaHinhThuc),

    CONSTRAINT FK_DONDATHANG_KHUVUC
        FOREIGN KEY (MaKhuVuc)
        REFERENCES KHUVUCGIAOHANG(MaKhuVuc),

    CONSTRAINT CK_DONDATHANG_TIENHANG
        CHECK (TienHang >= 0),

    CONSTRAINT CK_DONDATHANG_PHIGIAO
        CHECK (PhiGiaoHang >= 0),

    CONSTRAINT CK_DONDATHANG_TONGTIEN
        CHECK (TongTien >= 0)
);
GO


/* =========================================================
   11. CHI TIẾT ĐƠN ĐẶT HÀNG
   ========================================================= */
CREATE TABLE CT_DONDATHANG
(
    MaDonHang        VARCHAR(20)       NOT NULL,
    MaSP             VARCHAR(20)       NOT NULL,
    SoLuong          INT               NOT NULL,
    DonGia            DECIMAL(18,2)     NOT NULL,
    ThanhTien         AS (SoLuong * DonGia),

    CONSTRAINT PK_CT_DONDATHANG
        PRIMARY KEY (MaDonHang, MaSP),

    CONSTRAINT FK_CT_DONDATHANG_DONHANG
        FOREIGN KEY (MaDonHang)
        REFERENCES DONDATHANG(MaDonHang),

    CONSTRAINT FK_CT_DONDATHANG_SANPHAM
        FOREIGN KEY (MaSP)
        REFERENCES SANPHAM(MaSP),

    CONSTRAINT CK_CT_DONDATHANG_SOLUONG
        CHECK (SoLuong > 0),

    CONSTRAINT CK_CT_DONDATHANG_DONGIA
        CHECK (DonGia >= 0)
);
GO


/* =========================================================
   12. THẺ TÍN DỤNG
   ========================================================= */
CREATE TABLE THETINDUNG
(
    MaThe             VARCHAR(20)       NOT NULL,
    LoaiThe           VARCHAR(30)       NOT NULL,
    SoThe             VARCHAR(20)       NOT NULL,
    NgayHetHan        DATE              NOT NULL,
    TenChuThe         NVARCHAR(100)     NOT NULL,
    CSV               VARCHAR(4)        NOT NULL,

    CONSTRAINT PK_THETINDUNG
        PRIMARY KEY (MaThe),

    CONSTRAINT UQ_THETINDUNG_SOTHE
        UNIQUE (SoThe),

    CONSTRAINT CK_THETINDUNG_LOAITHE
        CHECK
        (
            LoaiThe IN
            (
                'VISA',
                'MASTERCARD',
                'DISCOVER',
                'AMERICAN EXPRESS'
            )
        )
);
GO


/* =========================================================
   13. THANH TOÁN
   ========================================================= */
CREATE TABLE THANHTOAN
(
    MaThanhToan       VARCHAR(20)       NOT NULL,
    MaDonHang         VARCHAR(20)       NOT NULL,
    MaThe             VARCHAR(20)       NOT NULL,
    NgayThanhToan     DATETIME          NOT NULL DEFAULT GETDATE(),
    SoTien            DECIMAL(18,2)     NOT NULL,
    TrangThai         NVARCHAR(50)      NOT NULL DEFAULT N'Chờ xác nhận',
    MaGiaoDich        VARCHAR(100)      NULL,

    CONSTRAINT PK_THANHTOAN
        PRIMARY KEY (MaThanhToan),

    CONSTRAINT FK_THANHTOAN_DONHANG
        FOREIGN KEY (MaDonHang)
        REFERENCES DONDATHANG(MaDonHang),

    CONSTRAINT FK_THANHTOAN_THETINDUNG
        FOREIGN KEY (MaThe)
        REFERENCES THETINDUNG(MaThe),

    CONSTRAINT CK_THANHTOAN_SOTIEN
        CHECK (SoTien >= 0)
);
GO


/* =========================================================
   14. PHÍ THANH TOÁN THEO LOẠI THẺ
   ========================================================= */
CREATE TABLE PHITHANHTOAN
(
    LoaiThe           VARCHAR(30)       NOT NULL,
    PhiThanhToan      DECIMAL(18,2)     NOT NULL,

    CONSTRAINT PK_PHITHANHTOAN
        PRIMARY KEY (LoaiThe),

    CONSTRAINT CK_PHITHANHTOAN_LOAITHE
        CHECK
        (
            LoaiThe IN
            (
                'VISA',
                'MASTERCARD',
                'DISCOVER',
                'AMERICAN EXPRESS'
            )
        ),

    CONSTRAINT CK_PHITHANHTOAN_PHI
        CHECK (PhiThanhToan >= 0)
);
GO


/* =========================================================
   15. EMAIL XÁC NHẬN ĐƠN HÀNG
   ========================================================= */
CREATE TABLE EMAILXACNHAN
(
    MaEmail           VARCHAR(20)       NOT NULL,
    MaDonHang         VARCHAR(20)       NOT NULL,
    EmailNhan         VARCHAR(150)      NOT NULL,
    NgayGui           DATETIME          NULL,
    TrangThai         NVARCHAR(50)      NOT NULL DEFAULT N'Chưa gửi',

    CONSTRAINT PK_EMAILXACNHAN
        PRIMARY KEY (MaEmail),

    CONSTRAINT FK_EMAILXACNHAN_DONHANG
        FOREIGN KEY (MaDonHang)
        REFERENCES DONDATHANG(MaDonHang)
);
GO