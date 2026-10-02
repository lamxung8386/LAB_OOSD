import javax.swing.*;
import java.awt.*;

public class GiaoDienThanhToan extends JFrame {

    public GiaoDienThanhToan(JFrame giaoDienCha) {

        setTitle("Thanh toán - e-SHOPPING");
        setSize(600, 550);
        setLocationRelativeTo(giaoDienCha);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
    }

    private void taoGiaoDien() {

        JPanel chinh = new JPanel(
                new GridBagLayout()
        );

        chinh.setBackground(Color.WHITE);

        JPanel khung = new JPanel(
                new GridLayout(7, 2, 10, 12)
        );

        khung.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 25, 30
                )
        );

        JLabel tieuDe = new JLabel(
                "THANH TOÁN"
        );

        tieuDe.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        khung.add(tieuDe);
        khung.add(new JLabel());

        JComboBox<String> loaiThe =
                new JComboBox<>(
                        new String[]{
                                "VISA",
                                "MASTERCARD",
                                "DISCOVER",
                                "AMERICAN EXPRESS"
                        }
                );

        JTextField soThe = new JTextField();
        JTextField ngayHetHan = new JTextField();
        JTextField tenChuThe = new JTextField();
        JTextField csv = new JTextField();

        khung.add(new JLabel("Loại thẻ:"));
        khung.add(loaiThe);

        khung.add(new JLabel("Số thẻ:"));
        khung.add(soThe);

        khung.add(new JLabel("Ngày hết hạn:"));
        khung.add(ngayHetHan);

        khung.add(new JLabel("Tên chủ thẻ:"));
        khung.add(tenChuThe);

        khung.add(new JLabel("Mã bảo mật:"));
        khung.add(csv);

        khung.add(new JLabel("Số tiền thanh toán:"));
        khung.add(new JLabel("17.400.000 VNĐ"));

        JButton thanhToan =
                new JButton("Thanh toán");

        JButton huy =
                new JButton("Hủy");

        khung.add(thanhToan);
        khung.add(huy);

        thanhToan.addActionListener(e -> {

            if (soThe.getText().trim().isEmpty()
                    || ngayHetHan.getText().trim().isEmpty()
                    || tenChuThe.getText().trim().isEmpty()
                    || csv.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập đầy đủ thông tin thẻ."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán thành công!"
            );

            new GiaoDienXacNhanDonHang(this)
                    .setVisible(true);

            dispose();
        });

        huy.addActionListener(e -> dispose());

        chinh.add(khung);

        add(chinh);
    }
}