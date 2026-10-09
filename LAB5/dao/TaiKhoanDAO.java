package dao;

import config.DBContext;
import model.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {

    // Kiem tra dang nhap
    public TaiKhoan checkLogin(String user, String pass) {
        String sql = "SELECT * FROM TAI_KHOAN WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TaiKhoan(
                    rs.getString("TenDangNhap"),
                    rs.getString("MatKhau"),
                    rs.getString("HoTen"),
                    rs.getString("Email"),
                    rs.getString("DienThoai"),
                    rs.getString("VaiTro")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Dang ky tai khoan moi
    public boolean register(TaiKhoan tk) {
        String sql = "INSERT INTO TAI_KHOAN (TenDangNhap, MatKhau, HoTen, Email, DienThoai, VaiTro) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getEmail());
            ps.setString(5, tk.getDienThoai());
            ps.setString(6, "KhachHang");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}