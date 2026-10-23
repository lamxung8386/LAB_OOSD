import java.io.Serializable;
import java.util.*;

/** Những thực thể nghiệp vụ xây dựng từ 6 sơ đồ Bài 7. */
public final class Model {
    private Model() {}

    public static class PhongBan implements Serializable {
        private static final long serialVersionUID = 1L;
        public PhongBan() {}
        public String ma, ten, truongPhong;
        public PhongBan(String ma, String ten, String truongPhong) { this.ma=ma; this.ten=ten; this.truongPhong=truongPhong; }
        public String toString() { return ma + " - " + ten; }
    }
    public static class NhanVien implements Serializable {
        private static final long serialVersionUID = 1L;
        public NhanVien() {}
        public String ma, hoTen, phongBan, ngayVao, sdt, trangThai;
        public NhanVien(String ma, String hoTen, String phongBan, String ngayVao, String sdt, String trangThai) {
            this.ma=ma; this.hoTen=hoTen; this.phongBan=phongBan; this.ngayVao=ngayVao; this.sdt=sdt; this.trangThai=trangThai;
        }
        public String toString() { return ma + " - " + hoTen; }
    }
    public static class QuyetDinhTuyenDung implements Serializable {
        private static final long serialVersionUID = 1L;
        public QuyetDinhTuyenDung() {}
        public String ma, nhanVien, ngayKy, batDau, ketThuc, dieuKhoan;
        public double luongThuViec;
        public QuyetDinhTuyenDung(String ma,String nv,String ngayKy,String bd,String kt,double luong,String dk) {
            this.ma=ma;this.nhanVien=nv;this.ngayKy=ngayKy;this.batDau=bd;this.ketThuc=kt;this.luongThuViec=luong;this.dieuKhoan=dk;
        }
    }
    public static class DanhGiaThuViec implements Serializable {
        private static final long serialVersionUID = 1L;
        public DanhGiaThuViec() {}
        public String ma, nhanVien, ngayDanhGia, ketQua, nhanXet;
        public DanhGiaThuViec(String ma,String nv,String ngay,String kq,String nx){this.ma=ma;this.nhanVien=nv;this.ngayDanhGia=ngay;this.ketQua=kq;this.nhanXet=nx;}
    }
    public static class HopDong implements Serializable {
        private static final long serialVersionUID = 1L;
        public HopDong() {}
        public String ma, nhanVien, loai, ngayKy, batDau, ketThuc;
        public HopDong(String ma,String nv,String loai,String ky,String bd,String kt){this.ma=ma;this.nhanVien=nv;this.loai=loai;this.ngayKy=ky;this.batDau=bd;this.ketThuc=kt;}
    }
    public static class QuyetDinhLuong implements Serializable {
        private static final long serialVersionUID = 1L;
        public QuyetDinhLuong() {}
        public String ma, nhanVien, hieuLuc;
        public double luongCoBan, phuCap, docHai;
        public QuyetDinhLuong(String ma,String nv,String hl,double l,double pc,double dh){this.ma=ma;this.nhanVien=nv;this.hieuLuc=hl;this.luongCoBan=l;this.phuCap=pc;this.docHai=dh;}
    }
    public static class QuyetDinhChucVu implements Serializable {
        private static final long serialVersionUID = 1L;
        public QuyetDinhChucVu() {}
        public String ma, nhanVien, chucVu, hieuLuc;
        public double phuCap;
        public QuyetDinhChucVu(String ma,String nv,String cv,String hl,double pc){this.ma=ma;this.nhanVien=nv;this.chucVu=cv;this.hieuLuc=hl;this.phuCap=pc;}
    }
    public static class HoSoBaoHiem implements Serializable {
        private static final long serialVersionUID = 1L;
        public HoSoBaoHiem() {}
        public String ma, hopDong, bhxh, bhyt;
        public double mucDongThang;
        public HoSoBaoHiem(String ma,String hd,String xh,String yt,double dong){this.ma=ma;this.hopDong=hd;this.bhxh=xh;this.bhyt=yt;this.mucDongThang=dong;}
    }
    public static class KeHoachPhanCong implements Serializable {
        private static final long serialVersionUID = 1L;
        public KeHoachPhanCong() {}
        public String ma, nhanVien, ngay, ca;
        public double soGio;
        public KeHoachPhanCong(String ma,String nv,String ngay,String ca,double soGio){this.ma=ma;this.nhanVien=nv;this.ngay=ngay;this.ca=ca;this.soGio=soGio;}
    }
    public static class ChamCong implements Serializable {
        private static final long serialVersionUID = 1L;
        public ChamCong() {}
        public String ma, nhanVien;
        public int thang, nam;
        public double ngayCong, gioLam;
        public ChamCong(String ma,String nv,int t,int n,double cong,double gio){this.ma=ma;this.nhanVien=nv;this.thang=t;this.nam=n;this.ngayCong=cong;this.gioLam=gio;}
    }
    public static class PhieuPhuTroi implements Serializable {
        private static final long serialVersionUID = 1L;
        public PhieuPhuTroi() {}
        public String ma, ngay, hinhThuc;
        public PhieuPhuTroi(String ma,String ngay,String hinhThuc){this.ma=ma;this.ngay=ngay;this.hinhThuc=hinhThuc;}
    }
    public static class ChiTietPhuTroi implements Serializable {
        private static final long serialVersionUID = 1L;
        public ChiTietPhuTroi() {}
        public String ma, phieu, nhanVien;
        public double gio;
        public ChiTietPhuTroi(String ma,String phieu,String nv,double gio){this.ma=ma;this.phieu=phieu;this.nhanVien=nv;this.gio=gio;}
    }
    public static class QuyDinhThuongPhat implements Serializable {
        private static final long serialVersionUID = 1L;
        public QuyDinhThuongPhat() {}
        public String ma, loai, doiTuong, maDoiTuong, lyDo, ngay, hinhThuc, trangThai, nguoiKy;
        public double soTien;
        public QuyDinhThuongPhat(String ma,String loai,String doiTuong,String maDT,String lyDo,String ngay,double tien,String hinhThuc) {
            this.ma=ma;this.loai=loai;this.doiTuong=doiTuong;this.maDoiTuong=maDT;this.lyDo=lyDo;
            this.ngay=ngay;this.soTien=tien;this.hinhThuc=hinhThuc;this.trangThai="CHO_DUYET";this.nguoiKy="";
        }
    }
    public static class PhieuThuChi implements Serializable {
        private static final long serialVersionUID = 1L;
        public PhieuThuChi() {}
        public String ma, quyetDinh, loai, ngay, ghiChu;
        public double soTien;
        public PhieuThuChi(String ma,String qd,String loai,String ngay,double tien,String ghiChu){this.ma=ma;this.quyetDinh=qd;this.loai=loai;this.ngay=ngay;this.soTien=tien;this.ghiChu=ghiChu;}
    }
    public static class BangLuong implements Serializable {
        private static final long serialVersionUID = 1L;
        public BangLuong() {}
        public String ma, nhanVien;
        public int thang, nam;
        public double luongCong, phuCap, tienPhuTroi, thuong, phat, baoHiem, thucLinh;
        public BangLuong(String ma,String nv,int t,int n,double lc,double pc,double pt,double thuong,double phat,double bh) {
            this.ma=ma;this.nhanVien=nv;this.thang=t;this.nam=n;this.luongCong=lc;this.phuCap=pc;this.tienPhuTroi=pt;
            this.thuong=thuong;this.phat=phat;this.baoHiem=bh;
            this.thucLinh=Math.max(0,lc+pc+pt+thuong-phat-bh);
        }
    }
    public static class NguoiDung implements Serializable {
        private static final long serialVersionUID = 1L;
        public NguoiDung() {}
        public String taiKhoan, matKhau, vaiTro, nhanVien;
        public NguoiDung(String tk,String mk,String role,String nv){this.taiKhoan=tk;this.matKhau=mk;this.vaiTro=role;this.nhanVien=nv;}
    }
    public static class DuLieu implements Serializable {
        private static final long serialVersionUID = 1L;
        public Map<String,PhongBan> phongBan = new LinkedHashMap<>();
        public Map<String,NhanVien> nhanVien = new LinkedHashMap<>();
        public Map<String,QuyetDinhTuyenDung> tuyenDung = new LinkedHashMap<>();
        public Map<String,DanhGiaThuViec> danhGia = new LinkedHashMap<>();
        public Map<String,HopDong> hopDong = new LinkedHashMap<>();
        public Map<String,QuyetDinhLuong> luong = new LinkedHashMap<>();
        public Map<String,QuyetDinhChucVu> chucVu = new LinkedHashMap<>();
        public Map<String,HoSoBaoHiem> baoHiem = new LinkedHashMap<>();
        public Map<String,KeHoachPhanCong> phanCong = new LinkedHashMap<>();
        public Map<String,ChamCong> chamCong = new LinkedHashMap<>();
        public Map<String,PhieuPhuTroi> phuTroi = new LinkedHashMap<>();
        public Map<String,ChiTietPhuTroi> chiTietPhuTroi = new LinkedHashMap<>();
        public Map<String,QuyDinhThuongPhat> thuongPhat = new LinkedHashMap<>();
        public Map<String,PhieuThuChi> thuChi = new LinkedHashMap<>();
        public Map<String,BangLuong> bangLuong = new LinkedHashMap<>();
        public Map<String,NguoiDung> nguoiDung = new LinkedHashMap<>();
        public int sequence = 100;
        public String id(String prefix) { return prefix + String.format("%04d", ++sequence); }
    }
}
