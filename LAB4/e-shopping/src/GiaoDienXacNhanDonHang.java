import javax.swing.*;
import java.awt.*;

public class GiaoDienXacNhanDonHang extends JFrame {

    public GiaoDienXacNhanDonHang(JFrame giaoDienCha) {

        setTitle("Xác nhận đơn hàng - e-SHOPPING");
        setSize(700, 600);
        setLocationRelativeTo(giaoDienCha);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
    }

    private void taoGiaoDien() {

        JPanel chinh = new JPanel(
                new BorderLayout(15, 15)
        );

        chinh.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 40, 30, 40
                )
        );

        chinh.setBackground(Color.WHITE);

        JLabel tieuDe = new JLabel(
                "ĐẶT HÀNG THÀNH CÔNG",
                SwingConstants.CENTER
        );

        tieuDe.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        tieuDe.setForeground(
                new Color(46, 125, 50)
        );

        JTextArea thongTin =
                new JTextArea();

        thongTin.setEditable(false);
        thongTin.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        thongTin.setText(
                "Mã đơn hàng: DH001\n\n" +
                "Sản phẩm:\n" +
                " - Laptop ABC x1\n" +
                " - Tai nghe ABC x2\n\n" +
                "Tiền hàng: 17.400.000 VNĐ\n" +
                "Phí giao hàng: 0 VNĐ\n" +
                "Tổng thanh toán: 17.400.000 VNĐ\n\n" +
                "Trạng thái: Chờ xử lý\n\n" +
                "Email xác nhận sẽ được gửi nếu khách hàng\n" +
                "cung cấp địa chỉ email."
        );

        JScrollPane cuon =
                new JScrollPane(thongTin);

        JButton dong =
                new JButton("Hoàn tất");

        dong.addActionListener(e -> dispose());

        chinh.add(tieuDe, BorderLayout.NORTH);
        chinh.add(cuon, BorderLayout.CENTER);
        chinh.add(dong, BorderLayout.SOUTH);

        add(chinh);
    }
}