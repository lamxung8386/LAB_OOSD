package vn.edu.benhan;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.sql.*;
import java.util.*;

/** Passwords are PBKDF2 salted, unlike cleartext demo credentials. */
public final class Auth {
    private Auth() {}
    public record User(String username, String fullName, String role) {}
    private static final SecureRandom RAND = new SecureRandom();
    private static final int ROUNDS = 130_000;

    public static void initDemoUsers() throws Exception {
        try (Connection c = Db.open()) {
            try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM dbo.TaiKhoan")) {
                rs.next(); if (rs.getInt(1) != 0) return;
            }
            // Only first boot of an empty demo database. Replace immediately for actual deployment.
            addUser(c, "admin", "admin123", "ADMIN", "Quản trị hệ thống");
            addUser(c, "bacsi", "bs123456", "BACSI", "Bác sĩ mẫu");
            addUser(c, "tiepnhan", "tn123456", "TIEPNHAN", "Nhân viên tiếp nhận");
            addUser(c, "chamsoc", "cs123456", "CHAMSOC", "Nhân viên chăm sóc");
        }
    }

    private static void addUser(Connection c, String name, String pass, String role, String fullname) throws Exception {
        byte[] salt = new byte[16]; RAND.nextBytes(salt);
        try (PreparedStatement p = c.prepareStatement(
                "INSERT dbo.TaiKhoan(TenDangNhap,MatKhauHash,Muoi,VaiTro,HoTen) VALUES (?,?,?,?,?)")) {
            p.setString(1, name);
            p.setString(2, hash(pass.toCharArray(), salt));
            p.setString(3, Base64.getEncoder().encodeToString(salt));
            p.setString(4, role);
            p.setString(5, fullname);
            p.executeUpdate();
        }
    }

    private static String hash(char[] pass, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(pass, salt, ROUNDS, 256);
        try {
            return Base64.getEncoder().encodeToString(
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());
        } finally { spec.clearPassword(); Arrays.fill(pass, '\0'); }
    }

    public static User login(String username, char[] password) throws Exception {
        try (Connection c = Db.open();
             PreparedStatement p = c.prepareStatement(
                     "SELECT HoTen,VaiTro,MatKhauHash,Muoi FROM dbo.TaiKhoan WHERE TenDangNhap=? AND KichHoat=1")) {
            p.setString(1, username.trim());
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next()) return null;
                byte[] salt = Base64.getDecoder().decode(rs.getString("Muoi"));
                String actual = hash(password.clone(), salt);
                byte[] a = Base64.getDecoder().decode(actual);
                byte[] b = Base64.getDecoder().decode(rs.getString("MatKhauHash"));
                if (!MessageDigest.isEqual(a, b)) return null;
                return new User(username.trim(), rs.getString("HoTen"), rs.getString("VaiTro"));
            }
        } finally { Arrays.fill(password, '\0'); }
    }

    public static boolean canEdit(User u, String section) {
        if (u.role().equals("ADMIN")) return true;
        return switch (u.role()) {
            case "BACSI" -> Set.of("PHIEUKHAM", "XETNGHIEM", "TOATHUOC", "CHITIETTOA").contains(section);
            case "TIEPNHAN" -> Set.of("BENHNHAN", "LICHHEN", "DONGPHI", "DANGKY").contains(section);
            case "CHAMSOC" -> Set.of("THANHTOAN").contains(section);
            default -> false;
        };
    }
}
