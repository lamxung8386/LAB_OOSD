import javax.swing.*;
import java.awt.*;

public class GiaoDienDatHang extends JFrame {

    private JTextField tenNguoiNhan;
    private JTextField diaChi;
    private JTextField dienThoai;

    private JComboBox<String> hinhThucGiao;
    private JComboBox<String> khuVuc;

    public GiaoDienDatHang(JFrame giaoDienCha) {

        setTitle("Đặt hàng - e-SHOPPING");
        setSize(650, 600);
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
                new GridLayout(8, 2, 10, 12)
        );

        khung.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 25, 30
                )
        );

        JLabel tieuDe = new JLabel(
                "THÔNG TIN ĐẶT HÀNG"
        );

        tieuDe.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        khung.add(tieuDe);
        khung.add(new JLabel());

        tenNguoiNhan = new JTextField();
        diaChi = new JTextField();
        dienThoai = new JTextField();

        hinhThucGiao = new JComboBox<>(
                new String[]{
                        "Giao hàng thường",
                        "Giao hàng nhanh",
                        "Giao hàng trong ngày"
                }
        );

        khuVuc = new JComboBox<>(
                new String[]{
                        "Khu vực 1",
                        "Khu vực 2",
                        "Khu vực 3"
                }
        );

        khung.add(new JLabel("Tên người nhận:"));
        khung.add(tenNguoiNhan);

        khung.add(new JLabel("Địa chỉ người nhận:"));
        khung.add(diaChi);

        khung.add(new JLabel("Điện thoại người nhận:"));
        khung.add(dienThoai);

        khung.add(new JLabel("Hình thức giao hàng:"));
        khung.add(hinhThucGiao);

        khung.add(new JLabel("Khu vực giao hàng:"));
        khung.add(khuVuc);

        khung.add(new JLabel("Tiền hàng:"));
        khung.add(new JLabel("17.400.000 VNĐ"));

        khung.add(new JLabel("Phí giao hàng:"));
        khung.add(new JLabel("0 VNĐ"));

        JButton tiepTuc = new JButton(
                "Tiếp tục thanh toán"
        );

        JButton huy = new JButton("Hủy");

        khung.add(tiepTuc);
        khung.add(huy);

        tiepTuc.addActionListener(e -> {

            if (tenNguoiNhan.getText().trim().isEmpty()
                    || diaChi.getText().trim().isEmpty()
                    || dienThoai.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập đầy đủ thông tin người nhận."
                );

                return;
            }

            new GiaoDienThanhToan(this).setVisible(true);
        });

        huy.addActionListener(e -> dispose());

        chinh.add(khung);

        add(chinh);
    }
}