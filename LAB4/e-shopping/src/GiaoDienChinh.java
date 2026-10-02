import javax.swing.*;
import java.awt.*;

public class GiaoDienChinh extends JFrame {

    private JPanel noiDung;

    public GiaoDienChinh() {
        setTitle("e-SHOPPING - Hệ thống mua sắm trực tuyến");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        taoGiaoDien();
    }

    private void taoGiaoDien() {

        // ================= THANH TIÊU ĐỀ =================
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(30, 136, 229));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel tenHeThong = new JLabel("e-SHOPPING");
        tenHeThong.setForeground(Color.WHITE);
        tenHeThong.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel moTa = new JLabel("Hệ thống mua sắm trực tuyến ABC");
        moTa.setForeground(Color.WHITE);
        moTa.setFont(new Font("Arial", Font.PLAIN, 15));

        JPanel thongTinTieuDe = new JPanel();
        thongTinTieuDe.setOpaque(false);
        thongTinTieuDe.setLayout(new BoxLayout(thongTinTieuDe, BoxLayout.Y_AXIS));
        thongTinTieuDe.add(tenHeThong);
        thongTinTieuDe.add(moTa);

        tieuDe.add(thongTinTieuDe, BorderLayout.WEST);

        // ================= MENU =================
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        menu.setBackground(new Color(245, 247, 250));

        JButton nutTrangChu = taoNut("Trang chủ");
        JButton nutSanPham = taoNut("Sản phẩm");
        JButton nutNhom = taoNut("Nhóm sản phẩm");
        JButton nutGioHang = taoNut("Giỏ hàng");
        JButton nutTaiKhoan = taoNut("Tài khoản");

        menu.add(nutTrangChu);
        menu.add(nutSanPham);
        menu.add(nutNhom);
        menu.add(nutGioHang);
        menu.add(nutTaiKhoan);

        // ================= NỘI DUNG =================
        noiDung = new JPanel(new BorderLayout());
        hienTrangChu();

        // ================= SỰ KIỆN =================
        nutTrangChu.addActionListener(e -> hienTrangChu());

        nutSanPham.addActionListener(e -> {
            new GiaoDienSanPham(this).setVisible(true);
        });

        nutNhom.addActionListener(e -> {
            new GiaoDienNhomSanPham(this).setVisible(true);
        });

        nutGioHang.addActionListener(e -> {
            new GiaoDienGioHang(this).setVisible(true);
        });

        nutTaiKhoan.addActionListener(e -> {
            new GiaoDienDangNhap(this).setVisible(true);
        });

        // ================= GHÉP GIAO DIỆN =================
        setLayout(new BorderLayout());
        add(tieuDe, BorderLayout.NORTH);

        JPanel khuVucTren = new JPanel(new BorderLayout());
        khuVucTren.add(menu, BorderLayout.CENTER);

        add(khuVucTren, BorderLayout.CENTER);

        // Thay lại bố cục để nội dung nằm bên dưới menu
        JPanel trungTam = new JPanel(new BorderLayout());
        trungTam.add(menu, BorderLayout.NORTH);
        trungTam.add(noiDung, BorderLayout.CENTER);

        add(trungTam, BorderLayout.CENTER);
    }

    private void hienTrangChu() {

        noiDung.removeAll();

        JPanel trangChu = new JPanel(new BorderLayout(20, 20));
        trangChu.setBackground(Color.WHITE);
        trangChu.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        // ================= LỜI CHÀO =================
        JPanel gioiThieu = new JPanel();
        gioiThieu.setBackground(Color.WHITE);
        gioiThieu.setLayout(new BoxLayout(gioiThieu, BoxLayout.Y_AXIS));

        JLabel chaoMung = new JLabel("CHÀO MỪNG ĐẾN e-SHOPPING");
        chaoMung.setFont(new Font("Arial", Font.BOLD, 30));
        chaoMung.setForeground(new Color(30, 136, 229));

        JLabel noiDungChao = new JLabel(
                "Mua sắm sản phẩm nhanh chóng và thuận tiện"
        );
        noiDungChao.setFont(new Font("Arial", Font.PLAIN, 17));

        gioiThieu.add(chaoMung);
        gioiThieu.add(Box.createVerticalStrut(8));
        gioiThieu.add(noiDungChao);

        // ================= NÚT CHỨC NĂNG =================
        JPanel cacChucNang = new JPanel(new GridLayout(1, 3, 20, 20));
        cacChucNang.setBackground(Color.WHITE);

        JButton sanPham = taoNutLon(
                "SẢN PHẨM",
                "Xem danh sách sản phẩm"
        );

        JButton nhomSanPham = taoNutLon(
                "NHÓM SẢN PHẨM",
                "Xem các nhóm sản phẩm"
        );

        JButton gioHang = taoNutLon(
                "GIỎ HÀNG",
                "Xem sản phẩm đã chọn"
        );

        sanPham.addActionListener(e ->
                new GiaoDienSanPham(this).setVisible(true)
        );

        nhomSanPham.addActionListener(e ->
                new GiaoDienNhomSanPham(this).setVisible(true)
        );

        gioHang.addActionListener(e ->
                new GiaoDienGioHang(this).setVisible(true)
        );

        cacChucNang.add(sanPham);
        cacChucNang.add(nhomSanPham);
        cacChucNang.add(gioHang);

        // ================= SẢN PHẨM NỔI BẬT =================
        JPanel sanPhamNoiBat = new JPanel(new BorderLayout());
        sanPhamNoiBat.setBackground(Color.WHITE);

        JLabel tieuDe = new JLabel("SẢN PHẨM NỔI BẬT");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 22));
        tieuDe.setForeground(new Color(50, 50, 50));

        JPanel danhSach = new JPanel(new GridLayout(1, 3, 20, 10));
        danhSach.setBackground(Color.WHITE);

        danhSach.add(taoTheSanPham(
                "Laptop ABC",
                "15.000.000 VNĐ"
        ));

        danhSach.add(taoTheSanPham(
                "Điện thoại ABC",
                "8.500.000 VNĐ"
        ));

        danhSach.add(taoTheSanPham(
                "Tai nghe ABC",
                "1.200.000 VNĐ"
        ));

        sanPhamNoiBat.add(tieuDe, BorderLayout.NORTH);
        sanPhamNoiBat.add(danhSach, BorderLayout.CENTER);

        trangChu.add(gioiThieu, BorderLayout.NORTH);
        trangChu.add(cacChucNang, BorderLayout.CENTER);
        trangChu.add(sanPhamNoiBat, BorderLayout.SOUTH);

        noiDung.add(trangChu, BorderLayout.CENTER);

        noiDung.revalidate();
        noiDung.repaint();
    }

    private JButton taoNut(String ten) {

        JButton nut = new JButton(ten);
        nut.setFont(new Font("Arial", Font.BOLD, 14));
        nut.setFocusPainted(false);
        nut.setBackground(Color.WHITE);
        nut.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 210, 210)
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 15, 8, 15
                        )
                )
        );

        return nut;
    }

    private JButton taoNutLon(String tieuDe, String moTa) {

        JButton nut = new JButton(
                "<html><center><b>" + tieuDe +
                "</b><br><br>" + moTa +
                "</center></html>"
        );

        nut.setFont(new Font("Arial", Font.PLAIN, 15));
        nut.setBackground(new Color(245, 248, 252));
        nut.setFocusPainted(false);
        nut.setBorder(
                BorderFactory.createLineBorder(
                        new Color(200, 210, 220)
                )
        );

        return nut;
    }

    private JPanel taoTheSanPham(String ten, String gia) {

        JPanel the = new JPanel();
        the.setBackground(new Color(248, 249, 251));
        the.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 220, 220)
                )
        );
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));

        JLabel tenSP = new JLabel(ten);
        tenSP.setFont(new Font("Arial", Font.BOLD, 17));
        tenSP.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel giaSP = new JLabel(gia);
        giaSP.setFont(new Font("Arial", Font.BOLD, 15));
        giaSP.setForeground(new Color(220, 60, 60));
        giaSP.setAlignmentX(Component.CENTER_ALIGNMENT);

        the.add(Box.createVerticalGlue());
        the.add(tenSP);
        the.add(Box.createVerticalStrut(10));
        the.add(giaSP);
        the.add(Box.createVerticalGlue());

        return the;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new GiaoDienChinh().setVisible(true);
        });
    }
}