import java.sql.Date;

public class PhieuDangKyDoan {
    private int maPhieuDoan;
    private String maTour;
    private String tenCoQuanDoan;
    private String diaChi;
    private String dienThoai;
    private String nguoiDaiDien;
    private int soNguoi;
    private Date ngayDiChon;
    private String diaDiemDon;
    private double tienCoc;

    // Constructors, Getters & Setters
    public PhieuDangKyDoan() {}

    public int getMaPhieuDoan() { return maPhieuDoan; }
    public void setMaPhieuDoan(int maPhieuDoan) { this.maPhieuDoan = maPhieuDoan; }
    public String getMaTour() { return maTour; }
    public void setMaTour(String maTour) { this.maTour = maTour; }
    public String getTenCoQuanDoan() { return tenCoQuanDoan; }
    public void setTenCoQuanDoan(String tenCoQuanDoan) { this.tenCoQuanDoan = tenCoQuanDoan; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
    public String getNguoiDaiDien() { return nguoiDaiDien; }
    public void setNguoiDaiDien(String nguoiDaiDien) { this.nguoiDaiDien = nguoiDaiDien; }
    public int getSoNguoi() { return soNguoi; }
    public void setSoNguoi(int soNguoi) { this.soNguoi = soNguoi; }
    public Date getNgayDiChon() { return ngayDiChon; }
    public void setNgayDiChon(Date ngayDiChon) { this.ngayDiChon = ngayDiChon; }
    public String getDiaDiemDon() { return diaDiemDon; }
    public void setDiaDiemDon(String diaDiemDon) { this.diaDiemDon = diaDiemDon; }
    public double getTienCoc() { return tienCoc; }
    public void setTienCoc(double tienCoc) { this.tienCoc = tienCoc; }
}