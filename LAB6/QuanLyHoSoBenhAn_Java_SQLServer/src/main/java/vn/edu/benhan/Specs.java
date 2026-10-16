package vn.edu.benhan;

import java.util.*;

/** Whitelist-driven schema: column names are never derived from user input. */
public final class Specs {
    private Specs() {}
    public record Field(String column, String title, String type, String values) {}
    public record Spec(String key, String title, String table, String pk, List<Field> fields,
                       boolean allowAdd, boolean allowUpdate, boolean allowDelete) {}

    static Field f(String column, String title, String type) { return new Field(column, title, type, ""); }
    static Field choices(String column, String title, String... options) {
        return new Field(column, title, "CHOICE", String.join("|", options));
    }
    static Field fk(String column, String title, String table, String pk, String display) {
        return new Field(column, title, "FK", table + ":" + pk + ":" + display);
    }
    static Spec s(String key, String title, String table, String pk, boolean a, boolean u, boolean d, Field... fields) {
        return new Spec(key, title, table, pk, Arrays.asList(fields), a, u, d);
    }

    public static final List<Spec> ALL = List.of(
        s("BENHNHAN", "Bệnh nhân", "BenhNhan", "MaBN", true, true, true,
            f("HoTen","Họ và tên","TEXT"), f("NgaySinh","Ngày sinh (yyyy-MM-dd)","DATE"),
            choices("GioiTinh","Giới tính","Nam","Nữ","Khác"), f("DiaChi","Địa chỉ","TEXT"),
            f("DienThoai","Điện thoại","TEXT"), f("Anh3x4","Đường dẫn ảnh 3x4","TEXT")),
        s("KHOA", "Khoa điều trị", "Khoa", "MaKhoa", true, true, true,
            f("TenKhoa","Tên khoa","TEXT"), f("MoTa","Mô tả","TEXT")),
        s("BACSI", "Danh sách bác sĩ", "BacSi", "MaBS", true, true, true,
            f("HoTen","Họ và tên","TEXT"), f("ChuyenMon","Chuyên môn","TEXT"),
            f("SoDienThoai","Điện thoại","TEXT"), fk("MaKhoa","Khoa","Khoa","MaKhoa","TenKhoa")),
        s("PHIEUDANGKY", "Phiếu đăng ký (xem)", "PhieuDangKy", "MaDK", false, false, false,
            fk("MaBN","Bệnh nhân","BenhNhan","MaBN","HoTen"), f("NgayDangKy","Ngày đăng ký","DATE"),
            f("GhiChu","Ghi chú","TEXT")),
        s("DONGPHI", "Đóng / gia hạn phí năm", "DongPhiNam", "MaPhi", true, false, false,
            fk("MaBN","Bệnh nhân","BenhNhan","MaBN","HoTen"), f("Nam","Năm","INT"),
            f("SoTien","Lệ phí (VNĐ)","MONEY"), f("NgayDong","Ngày đóng","DATE"), f("GhiChu","Ghi chú","TEXT")),
        s("HOSO", "Hồ sơ bệnh án (xem)", "HoSoBenhAn", "MaHS", false, false, false,
            fk("MaBN","Bệnh nhân","BenhNhan","MaBN","HoTen"), fk("MaDK","Phiếu đăng ký","PhieuDangKy","MaDK","MaDK"),
            f("NgayLap","Ngày lập","DATE"), f("GhiChu","Ghi chú","TEXT")),
        s("LICHHEN", "Lịch hẹn khám", "LichHen", "MaHen", true, true, true,
            fk("MaBN","Bệnh nhân","BenhNhan","MaBN","HoTen"), fk("MaBS","Bác sĩ (tùy chọn)","BacSi","MaBS","HoTen"),
            f("NgayGioHen","Ngày giờ hẹn (yyyy-MM-dd HH:mm)","DATETIME"),
            choices("LoaiKham","Loại khám","Khám bệnh","Định kỳ quý"),
            choices("TrangThai","Trạng thái","Đã đặt","Đã khám","Đã hủy"), f("GhiChu","Ghi chú","TEXT")),
        s("PHIEUKHAM", "Phiếu khám", "PhieuKham", "MaPK", true, true, true,
            fk("MaHS","Hồ sơ bệnh án","HoSoBenhAn","MaHS","MaHS"), fk("MaBS","Bác sĩ","BacSi","MaBS","HoTen"),
            fk("MaKhoa","Khoa điều trị","Khoa","MaKhoa","TenKhoa"),
            f("NgayGioKham","Ngày giờ khám","DATETIME"), f("TrieuChung","Triệu chứng / huyết áp","TEXT"),
            f("ChanDoan","Chẩn đoán","TEXT"), choices("LoaiKham","Loại khám","Khám bệnh","Định kỳ quý"),
            choices("TrangThai","Trạng thái","Đang khám","Chờ thanh toán","Đã lưu")),
        s("XETNGHIEM", "Xét nghiệm", "XetNghiem", "MaXN", true, true, true,
            fk("MaPK","Phiếu khám","PhieuKham","MaPK","MaPK"), f("TenXetNghiem","Tên xét nghiệm","TEXT"),
            f("NgayChiDinh","Ngày chỉ định","DATETIME"), f("KetQua","Kết quả","TEXT"),
            choices("TrangThai","Trạng thái","Chờ kết quả","Đã có kết quả")),
        s("TOATHUOC", "Toa thuốc", "ToaThuoc", "MaToa", true, true, true,
            fk("MaPK","Phiếu khám","PhieuKham","MaPK","MaPK"), f("NgayKe","Ngày kê","DATE"), f("LoiDan","Lời dặn","TEXT")),
        s("CHITIETTOA", "Chi tiết toa thuốc", "ChiTietToaThuoc", "MaCT", true, true, true,
            fk("MaToa","Toa thuốc","ToaThuoc","MaToa","MaToa"),
            f("TenThuoc","Tên thuốc","TEXT"), f("LieuDung","Liều dùng","TEXT"),
            f("SoLuong","Số lượng","INT"), f("CachDung","Cách dùng","TEXT")),
        s("THANHTOAN", "Thanh toán viện phí (xem)", "ThanhToanVienPhi", "MaTT", false, false, false,
            fk("MaPK","Phiếu khám","PhieuKham","MaPK","MaPK"), f("SoTien","Số tiền","MONEY"),
            f("NgayThanhToan","Ngày thanh toán","DATETIME"),
            choices("TrangThai","Trạng thái","Đã thanh toán","Miễn phí"), f("GhiChu","Ghi chú","TEXT"))
    );

    public static Spec byKey(String key) {
        return ALL.stream().filter(s -> s.key().equals(key)).findFirst().orElseThrow();
    }
}
