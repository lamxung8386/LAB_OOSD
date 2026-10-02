import javax.swing.*;
import java.awt.*;

public class GiaoDienNhomSanPham extends JFrame {

    public GiaoDienNhomSanPham(JFrame giaoDienCha) {

        setTitle("Nhóm sản phẩm - e-SHOPPING");
        setSize(800, 550);
        setLocationRelativeTo(giaoDienCha);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        taoGiaoDien();
    }

    private void taoGiaoDien() {

        JPanel chinh = new JPanel(new BorderLayout(15, 15));
        chinh.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        chinh.setBackground(Color.WHITE);

        JLabel tieuDe = new JLabel("NHÓM SẢN PHẨM");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        tieuDe.setForeground(new Color(30, 136, 229));

        String[] cot = {
                "Mã nhóm",
                "Tên nhóm"
        };

        Object[][] duLieu = {
                {"N001", "Laptop"},
                {"N002", "Điện thoại"},
                {"N003", "Phụ kiện"},
                {"N004", "Thiết bị gia dụng"}
        };

        JTable bang = new JTable(duLieu, cot);
        bang.setRowHeight(32);

        JScrollPane cuon = new JScrollPane(bang);

        JButton xem = new JButton("Xem sản phẩm");
        JButton dong = new JButton("Đóng");

        xem.addActionListener(e -> {

            int dongChon = bang.getSelectedRow();

            if (dongChon == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn một nhóm sản phẩm."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đang xem sản phẩm thuộc nhóm: "
                    + bang.getValueAt(dongChon, 1)
            );
        });

        dong.addActionListener(e -> dispose());

        JPanel nut = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        nut.add(xem);
        nut.add(dong);

        chinh.add(tieuDe, BorderLayout.NORTH);
        chinh.add(cuon, BorderLayout.CENTER);
        chinh.add(nut, BorderLayout.SOUTH);

        add(chinh);
    }
}