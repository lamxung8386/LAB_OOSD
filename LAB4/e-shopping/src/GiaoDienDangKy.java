import javax.swing.*;
import java.awt.*;

public class GiaoDienDangKy extends JFrame {

    public GiaoDienDangKy(JFrame giaoDienCha) {

        setTitle("Đăng ký tài khoản - e-SHOPPING");
        setSize(550, 600);
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
                new GridLayout(9, 2, 10, 10)
        );

        khung.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 25, 30
                )
        );

        JLabel tieuDe = new JLabel(
                "ĐĂNG KÝ TÀI KHOẢN"
        );

        tieuDe.setFont(
                new Font("Arial", Font.BOLD, 23)
        );

        khung.add(tieuDe);
        khung.add(new JLabel());

        JTextField hoTen = new JTextField();
        JTextField ngaySinh = new JTextField();
        JTextField cmnd = new JTextField();
        JTextField diaChi = new JTextField();
        JTextField dienThoai = new JTextField();
        JTextField tenDangNhap = new JTextField();
        JPasswordField matKhau = new JPasswordField();
        JTextField email = new JTextField();

        khung.add(new JLabel("Họ tên:"));
        khung.add(hoTen);

        khung.add(new JLabel("Ngày sinh:"));
        khung.add(ngaySinh);

        khung.add(new JLabel("CMND/Passport:"));
        khung.add(cmnd);

        khung.add(new JLabel("Địa chỉ:"));
        khung.add(diaChi);

        khung.add(new JLabel("Điện thoại:"));
        khung.add(dienThoai);

        khung.add(new JLabel("Tên đăng nhập:"));
        khung.add(tenDangNhap);

        khung.add(new JLabel("Mật khẩu:"));
        khung.add(matKhau);

        khung.add(new JLabel("Email:"));
        khung.add(email);

        JButton dangKy = new JButton("Đăng ký");
        JButton huy = new JButton("Hủy");

        khung.add(dangKy);
        khung.add(huy);

        dangKy.addActionListener(e -> {

            if (hoTen.getText().trim().isEmpty()
                    || tenDangNhap.getText().trim().isEmpty()
                    || matKhau.getPassword().length == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập các thông tin bắt buộc."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đăng ký tài khoản thành công!"
            );

            dispose();
        });

        huy.addActionListener(e -> dispose());

        chinh.add(khung);

        add(chinh);
    }
}