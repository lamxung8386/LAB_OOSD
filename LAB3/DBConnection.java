import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException(
                    "Chưa thêm mssql-jdbc-*.jar vào classpath.",
                    ex
            );
        }

        String dbURL =
            "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QLKhachSan;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

        String user = "sa";
        String pass = "123456";

        return DriverManager.getConnection(dbURL, user, pass);
    }
}