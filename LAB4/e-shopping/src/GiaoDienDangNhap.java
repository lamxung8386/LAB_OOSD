import javax.swing.*;
import java.awt.*;

public class GiaoDienDangNhap extends JFrame {

    public GiaoDienDangNhap(JFrame giaoDienCha) {

        setTitle("Đăng nhập - e-SHOPPING");
        setSize(450, 350);
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
                new GridLayout(5, 1, 10, 10)
        );

        khung.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 35, 25, 35
                )
        );

        JLabel tieuDe = new JLabel(
                "ĐĂNG NHẬP",
                SwingConstants.CENTER
        );

        tieuDe.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        JTextField tenDangNhap = new JTextField();
        JPasswordField matKhau = new JPasswordField();

        JButton dangNhap = new JButton("Đăng nhập");
        JButton dangKy = new JButton("Đăng ký");

        khung.add(tieuDe);
        khung.add(taoDong(
                "Tên đăng nhập:",
                tenDangNhap
        ));
        khung.add(taoDong(
                "Mật khẩu:",
                matKhau
        ));

        JPanel nut = new JPanel();
        nut.add(dangNhap);
        nut.add(dangKy);

        khung.add(nut);

        dangNhap.addActionListener(e -> {

            if (tenDangNhap.getText().trim().isEmpty()
                    || matKhau.getPassword().length == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập đầy đủ thông tin."
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Đăng nhập thành công!"
                );

                dispose();
            }
        });

        dangKy.addActionListener(e -> {

            new GiaoDienDangKy(this).setVisible(true);
        });

        chinh.add(khung);

        add(chinh);
    }

    private JPanel taoDong(
            String nhan,
            JComponent oNhap) {

        JPanel dong = new JPanel(
                new BorderLayout(10, 0)
        );

        dong.setBackground(Color.WHITE);

        JLabel label = new JLabel(nhan);
        label.setPreferredSize(
                new Dimension(120, 30)
        );

        dong.add(label, BorderLayout.WEST);
        dong.add(oNhap, BorderLayout.CENTER);

        return dong;
    }
}