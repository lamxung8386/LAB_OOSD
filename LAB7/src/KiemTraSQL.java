import java.nio.file.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/** Chạy file chay_kiem_tra_SQL_windows.bat trước khi mở ứng dụng SQL. */
public class KiemTraSQL {
    public static void main(String[] args) {
        try {
            Properties p=new Properties();
            try (InputStream in=Files.newInputStream(Path.of("ketnoi.properties"))) {p.load(in);}
            SqlLuuTru sql=new SqlLuuTru(p);
            try(Connection c=sql.ketNoi(); Statement s=c.createStatement()) {
                try(ResultSet r=s.executeQuery("SELECT DB_NAME() AS TenCSDL, @@SERVERNAME AS TenServer")) {
                    if(r.next())System.out.printf("KET NOI THANH CONG: %s / %s%n",r.getString(2),r.getString(1));
                }
                try(ResultSet r=s.executeQuery("SELECT COUNT(*) FROM sys.tables WHERE schema_id=SCHEMA_ID('dbo')")){
                    if(r.next())System.out.println("SO BANG TRONG CSDL: "+r.getInt(1));
                }
                try(ResultSet r=s.executeQuery("SELECT COUNT(*) FROM dbo.NhanVien")){
                    if(r.next())System.out.println("SO NHAN VIEN: "+r.getInt(1));
                }
            }
        } catch(Exception e) {
            System.err.println("KHONG KET NOI DUOC SQL SERVER: "+e.getMessage());
            System.err.println("Kiem tra 4 buoc: (1) tao bang SQL; (2) JDBC Driver + DLL x64 trong lib; (3) bat TCP/IP va port SQL; (4) tai khoan Windows/SQL dung.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
