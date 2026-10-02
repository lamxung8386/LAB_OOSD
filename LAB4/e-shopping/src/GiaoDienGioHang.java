import javax.swing.*;
import java.awt.*;

public class GiaoDienGioHang extends JFrame {

    private JLabel tongTien;

    public GiaoDienGioHang(JFrame giaoDienCha) {

        setTitle("Giỏ hàng - e-SHOPPING");
        setSize(900, 600);
        setLocationRelativeTo(giaoDienCha);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
    }

    private void taoGiaoDien() {

        JPanel chinh = new JPanel(new BorderLayout(15, 15));
        chinh.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        chinh.setBackground(Color.WHITE);

        JLabel tieuDe = new JLabel("GIỎ HÀNG");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        tieuDe.setForeground(new Color(30, 136, 229));

        String[] cot = {
                "Mã SP",
                "Tên sản phẩm",
                "Đơn giá",
                "Số lượng",
                "Thành tiền"
        };

        Object[][] duLieu = {
                {"SP001", "Laptop ABC", "15.000.000", 1,
                        "15.000.000"},
                {"SP003", "Tai nghe ABC", "1.200.000", 2,
                        "2.400.000"}
        };

        JTable bang = new JTable(duLieu, cot);
        bang.setRowHeight(30);

        JScrollPane cuon = new JScrollPane(bang);

        tongTien = new JLabel("Tổng tiền: 17.400.000 VNĐ");
        tongTien.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JButton capNhat = new JButton("Cập nhật số lượng");
        JButton xoa = new JButton("Xóa sản phẩm");
        JButton datHang = new JButton("Đặt hàng");
        JButton dong = new JButton("Đóng");

        xoa.addActionListener(e -> {

            int dongChon = bang.getSelectedRow();

            if (dongChon == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn sản phẩm cần xóa."
                );
                return;
            }

            ((javax.swing.table.DefaultTableModel)
                    bang.getModel()).removeRow(dongChon);
        });

        capNhat.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "Đã cập nhật số lượng sản phẩm."
            );
        });

        datHang.addActionListener(e -> {

            new GiaoDienDatHang(this).setVisible(true);
        });

        dong.addActionListener(e -> dispose());

        JPanel phiaDuoi = new JPanel(
                new BorderLayout()
        );

        phiaDuoi.add(tongTien, BorderLayout.WEST);

        JPanel cacNut = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        cacNut.add(capNhat);
        cacNut.add(xoa);
        cacNut.add(datHang);
        cacNut.add(dong);

        phiaDuoi.add(cacNut, BorderLayout.EAST);

        chinh.add(tieuDe, BorderLayout.NORTH);
        chinh.add(cuon, BorderLayout.CENTER);
        chinh.add(phiaDuoi, BorderLayout.SOUTH);

        add(chinh);
    }
}