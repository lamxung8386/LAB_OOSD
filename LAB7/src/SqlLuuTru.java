import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Lưu các thực thể Java vào các bảng quan hệ trong SQL Server bằng JDBC.
 *  Bản học tập chạy đơn máy: ghi snapshot toàn bộ dữ liệu trong 1 transaction.
 *  KHÔNG sử dụng đồng thời nhiều ứng dụng ở chế độ này. */
public final class SqlLuuTru {
    private final String url, user, password;
    private final List<Bang> cacBang = List.of(
        new Bang("PhongBan", "phongBan", Model.PhongBan.class, "ma"),
        new Bang("NhanVien", "nhanVien", Model.NhanVien.class, "ma"),
        new Bang("QuyetDinhTuyenDung", "tuyenDung", Model.QuyetDinhTuyenDung.class, "ma"),
        new Bang("DanhGiaThuViec", "danhGia", Model.DanhGiaThuViec.class, "ma"),
        new Bang("HopDong", "hopDong", Model.HopDong.class, "ma"),
        new Bang("QuyetDinhLuong", "luong", Model.QuyetDinhLuong.class, "ma"),
        new Bang("QuyetDinhChucVu", "chucVu", Model.QuyetDinhChucVu.class, "ma"),
        new Bang("HoSoBaoHiem", "baoHiem", Model.HoSoBaoHiem.class, "ma"),
        new Bang("KeHoachPhanCong", "phanCong", Model.KeHoachPhanCong.class, "ma"),
        new Bang("ChamCong", "chamCong", Model.ChamCong.class, "ma"),
        new Bang("PhieuPhuTroi", "phuTroi", Model.PhieuPhuTroi.class, "ma"),
        new Bang("ChiTietPhuTroi", "chiTietPhuTroi", Model.ChiTietPhuTroi.class, "ma"),
        new Bang("QuyDinhThuongPhat", "thuongPhat", Model.QuyDinhThuongPhat.class, "ma"),
        new Bang("PhieuThuChi", "thuChi", Model.PhieuThuChi.class, "ma"),
        new Bang("BangLuong", "bangLuong", Model.BangLuong.class, "ma"),
        new Bang("NguoiDung", "nguoiDung", Model.NguoiDung.class, "taiKhoan")
    );
    public SqlLuuTru(Properties cfg) {
        url = cfg.getProperty("jdbc.url", "").trim();
        user = cfg.getProperty("jdbc.user", "").trim();
        password = cfg.getProperty("jdbc.password", "");
        if (url.isEmpty()) throw new IllegalStateException("Chưa điền jdbc.url trong ketnoi.properties");
        try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Chưa có JDBC Driver. Tải mssql-jdbc-13.6.0.jre11.jar vào thư mục lib và chạy chay_SQLServer_windows.bat", ex);
        }
        try (Connection c = ketNoi(); Statement s = c.createStatement()) {
            s.executeQuery("SELECT TOP (1) [ID] FROM dbo.HeThong").close();
        } catch (SQLException ex) { throw loiSql("Không kết nối/đọc được CSDL. Hãy chạy sql/01_tao_bang.sql và kiểm tra TCP/IP, tài khoản Windows, JDBC Driver", ex); }
    }
    public Connection ketNoi() throws SQLException {
        return user.isEmpty() ? DriverManager.getConnection(url) : DriverManager.getConnection(url,user,password);
    }
    public Model.DuLieu doc() {
        Model.DuLieu db = new Model.DuLieu();
        try (Connection c = ketNoi()) {
            try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT SoThuTu FROM dbo.HeThong WHERE ID=1")) {
                if (r.next()) db.sequence = r.getInt(1);
            }
            for (Bang b : cacBang) {
                Map<String, Object> map = b.getMap(db);
                try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM dbo.["+b.ten+"] ORDER BY ["+b.khoa+"]")) {
                    while (r.next()) {
                        Object obj = b.cls.getDeclaredConstructor().newInstance();
                        for (Field f : b.cacTruong()) {
                            Class<?> t = f.getType();
                            if (t == String.class) {
                                if (LA_NGAY.contains(f.getName())) {
                                    java.sql.Date value=r.getDate(f.getName());
                                    f.set(obj,value==null?"":value.toLocalDate().toString());
                                } else { String value=r.getString(f.getName()); f.set(obj,value==null?"":value); }
                            } else if (t == int.class) f.setInt(obj,r.getInt(f.getName()));
                            else if (t == double.class) f.setDouble(obj,r.getDouble(f.getName()));
                        }
                        String id=(String)b.cls.getField(b.khoa).get(obj);
                        map.put(id,obj);
                    }
                }
            }
            return db;
        } catch (Exception ex) { throw new IllegalStateException("Đọc dữ liệu SQL Server thất bại: "+ex.getMessage(), ex); }
    }
    public synchronized void ghi(Model.DuLieu db) {
        try (Connection c = ketNoi()) {
            c.setAutoCommit(false);
            try {
                // Xóa từ bảng con về bảng cha để tuân thủ FOREIGN KEY. Tất cả nằm trong transaction.
                for (int i=cacBang.size()-1;i>=0;i--) {
                    try (Statement s=c.createStatement()) { s.executeUpdate("DELETE FROM dbo.["+cacBang.get(i).ten+"]"); }
                }
                for (Bang b : cacBang) {
                    Field[] fields=b.cacTruong();
                    StringJoiner names=new StringJoiner(","), placeholders=new StringJoiner(",");
                    for(Field f:fields){names.add("["+f.getName()+"]");placeholders.add("?");}
                    String sql="INSERT INTO dbo.["+b.ten+"] ("+names+") VALUES ("+placeholders+")";
                    try(PreparedStatement ps=c.prepareStatement(sql)){
                        for(Object obj:b.getMap(db).values()){
                            int pos=1;
                            for(Field f:fields){
                                Class<?> t=f.getType();
                                if(t==String.class){
                                    String v=(String)f.get(obj);
                                    if(LA_NGAY.contains(f.getName())) {
                                        if(v==null||v.isBlank()) ps.setNull(pos,Types.DATE);
                                        else ps.setDate(pos,java.sql.Date.valueOf(v));
                                    } else if(v==null||(b.ten.equals("NguoiDung")&&f.getName().equals("nhanVien")&&v.isBlank())) ps.setNull(pos,Types.NVARCHAR);
                                    else ps.setString(pos,v);
                                }
                                else if(t==int.class) ps.setInt(pos,f.getInt(obj));
                                else if(t==double.class) ps.setDouble(pos,f.getDouble(obj));
                                pos++;
                            }
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                try (PreparedStatement ps=c.prepareStatement("UPDATE dbo.HeThong SET SoThuTu=? WHERE ID=1")) {
                    ps.setInt(1,db.sequence);ps.executeUpdate();
                }
                c.commit();
            } catch(Exception ex) {
                try {c.rollback();}catch(SQLException roll){ex.addSuppressed(roll);}
                throw ex;
            }
        } catch (Exception ex) { throw new IllegalStateException("Không lưu được vào SQL Server (đã rollback): "+ex.getMessage(), ex); }
    }
    private static final Set<String> LA_NGAY=Set.of("ngayVao","ngayKy","batDau","ketThuc","ngayDanhGia","hieuLuc","ngay");
    private record Bang(String ten,String bien,Class<?> cls,String khoa) {
        @SuppressWarnings("unchecked")
        Map<String,Object> getMap(Model.DuLieu d) throws Exception {return (Map<String,Object>) Model.DuLieu.class.getField(bien).get(d);}
        Field[] cacTruong() { return Arrays.stream(cls.getFields()).filter(f->!Modifier.isStatic(f.getModifiers())).toArray(Field[]::new); }
    }
    private static IllegalStateException loiSql(String s,SQLException ex){
        return new IllegalStateException(s+"\nMã lỗi: "+ex.getErrorCode()+"; "+ex.getMessage(),ex);
    }
}
