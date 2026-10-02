import javax.swing.*;
import java.awt.*;

public class GiaoDienSanPham extends JFrame {

    private JFrame giaoDienCha;

    public GiaoDienSanPham(JFrame giaoDienCha) {

        this.giaoDienCha = giaoDienCha;

        setTitle("Sản phẩm - e-SHOPPING");
        setSize(1000, 650);
        setLocationRelativeTo(giaoDienCha);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
    }

    private void taoGiaoDien() {

        JPanel chinh = new JPanel(new BorderLayout(15, 15));
        chinh.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        chinh.setBackground(Color.WHITE);

        JLabel tieuDe = new JLabel("DANH SÁCH SẢN PHẨM");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        tieuDe.setForeground(new Color(30, 136, 229));

        chinh.add(tieuDe, BorderLayout.NORTH);

        String[] cot = {
                "Mã SP",
                "Tên sản phẩm",
                "Nhà sản xuất",
                "Nhóm",
                "Giá bán",
                "Tình trạng"
        };

        Object[][] duLieu = {
                {"SP001", "Laptop ABC", "ABC", "Laptop",
                        "15.000.000", "Còn hàng"},
                {"SP002", "Điện thoại ABC", "ABC", "Điện thoại",
                        "8.500.000", "Còn hàng"},
                {"SP003", "Tai nghe ABC", "ABC", "Phụ kiện",
                        "1.200.000", "Còn hàng"},
                {"SP004", "Bàn phím ABC", "ABC", "Phụ kiện",
                        "800.000", "Hết hàng"}
        };

        JTable bang = new JTable(duLieu, cot);
        bang.setRowHeight(30);
        bang.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        JScrollPane cuon = new JScrollPane(bang);

        JPanel nut = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton xemChiTiet = new JButton("Xem chi tiết");
        JButton themGio = new JButton("Thêm vào giỏ hàng");
        JButton dong = new JButton("Đóng");

        nut.add(xemChiTiet);
        nut.add(themGio);
        nut.add(dong);

        xemChiTiet.addActionListener(e -> {

            int dongChon = bang.getSelectedRow();

            if (dongChon == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn sản phẩm."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Tên sản phẩm: " +
                    bang.getValueAt(dongChon, 1) +
                    "\nGiá bán: " +
                    bang.getValueAt(dongChon, 4) +
                    " VNĐ" +
                    "\nTình trạng: " +
                    bang.getValueAt(dongChon, 5),
                    "Chi tiết sản phẩm",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        themGio.addActionListener(e -> {

            int dongChon = bang.getSelectedRow();

            if (dongChon == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn sản phẩm."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đã thêm \"" +
                    bang.getValueAt(dongChon, 1) +
                    "\" vào giỏ hàng."
            );
        });

        dong.addActionListener(e -> dispose());

        chinh.add(cuon, BorderLayout.CENTER);
        chinh.add(nut, BorderLayout.SOUTH);

        add(chinh);
    }
}