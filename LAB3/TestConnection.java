import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        try {
            Connection conn = DBConnection.getConnection();

            if (conn != null) {
                System.out.println("KẾT NỐI SQL SERVER THÀNH CÔNG!");

                conn.close();
            }

        } catch (Exception e) {
            System.out.println("KẾT NỐI THẤT BẠI!");
            e.printStackTrace();
        }
    }
}