package vn.edu.benhan;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Read local config.properties, then create short-lived JDBC connections. */
public final class Db {
    private static final Properties CONFIG = new Properties();
    static {
        Path path = Paths.get("config.properties");
        try (InputStream in = Files.exists(path) ? Files.newInputStream(path)
                : Db.class.getResourceAsStream("/config.properties")) {
            if (in != null) CONFIG.load(in);
        } catch (IOException ex) { throw new ExceptionInInitializerError(ex); }
    }
    private Db() {}

    public static Connection open() throws SQLException {
        String host = CONFIG.getProperty("server", "localhost").trim();
        String port = CONFIG.getProperty("port", "1433").trim();
        String database = CONFIG.getProperty("database", "QuanLyHoSoBenhAn").trim();
        String url = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database
                + ";encrypt=" + CONFIG.getProperty("encrypt", "true")
                + ";trustServerCertificate=" + CONFIG.getProperty("trustServerCertificate", "true")
                + ";loginTimeout=" + CONFIG.getProperty("loginTimeout", "6") + ";";
        Properties credentials = new Properties();
        credentials.setProperty("user", CONFIG.getProperty("user", "sa").trim());
        credentials.setProperty("password", CONFIG.getProperty("password", ""));
        return DriverManager.getConnection(url, credentials);
    }

    public static int count(String table) throws SQLException {
        try (Connection c = open(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM dbo." + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public static void withTransaction(SqlWork work) throws Exception {
        try (Connection c = open()) {
            c.setAutoCommit(false);
            try { work.run(c); c.commit(); }
            catch (Exception ex) { c.rollback(); throw ex; }
        }
    }
    @FunctionalInterface public interface SqlWork { void run(Connection c) throws Exception; }
}
