import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class QuanLyKhachSan extends JFrame implements ActionListener {

    private JButton btnDanhMuc, btnPhongTienNghi, btnDatNhanPhong;
    private JButton btnDichVu, btnThanhToan, btnThongKe, btnThoat;

    public QuanLyKhachSan() {
        setTitle("Quản lý khách sạn");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ KHÁCH SẠN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0x15, 0x4B, 0x7A));
        lblTitle.setBounds(180, 45, 540, 50);
        add(lblTitle);

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);

        btnDanhMuc = createButton("Danh mục", buttonFont, 40, 130, 240, 65);
        btnPhongTienNghi = createButton("Phòng - Tiện nghi", buttonFont, 330, 130, 240, 65);
        btnDatNhanPhong = createButton("Đặt / Nhận phòng", buttonFont, 620, 130, 240, 65);
        btnDichVu = createButton("Sử dụng dịch vụ", buttonFont, 40, 215, 240, 65);
        btnThanhToan = createButton("Trả phòng - Thanh toán", buttonFont, 330, 215, 240, 65);
        btnThongKe = createButton("Thống kê", buttonFont, 620, 215, 240, 65);
        btnThoat = createButton("Thoát", buttonFont, 330, 300, 240, 65);

        add(btnDanhMuc);
        add(btnPhongTienNghi);
        add(btnDatNhanPhong);
        add(btnDichVu);
        add(btnThanhToan);
        add(btnThongKe);
        add(btnThoat);
    }

    private JButton createButton(String text, Font font, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBounds(x, y, width, height);
        button.setFocusable(false);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnDanhMuc) moFormDanhMuc();
        else if (source == btnPhongTienNghi) moFormPhongTienNghi();
        else if (source == btnDatNhanPhong) moFormDatNhanPhong();
        else if (source == btnDichVu) moFormSuDungDichVu();
        else if (source == btnThanhToan) moFormTraPhongThanhToan();
        else if (source == btnThongKe) moFormThongKe();
        else if (source == btnThoat) {
            if (JOptionPane.showConfirmDialog(this, "Bạn có muốn thoát chương trình không?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    // 1. DANH MỤC KHÁCH HÀNG
    private void moFormDanhMuc() {
        JDialog dialog = new JDialog(this, "Danh Sách Khách Hàng", true);
        dialog.setSize(750, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Mã Khách", "Họ Tên", "Số CMND/CCCD", "Quốc Tịch", "Số Điện Thoại"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM KhachHang");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaKhach"),
                        rs.getString("HoTen"),
                        rs.getString("SoCMND"),
                        rs.getString("QuocTich"),
                        rs.getString("SoDienThoai")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối cơ sở dữ liệu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi tải CSDL: " + ex.getMessage());
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    // 2. PHÒNG & TIỆN NGHI
    private void moFormPhongTienNghi() {
        JDialog dialog = new JDialog(this, "Danh Sách Phòng & Tiện Nghi", true);
        dialog.setSize(750, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Số Phòng", "Sức Chứa", "Đơn Giá/Ngày", "Trạng Thái", "Mã Khu Vực"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SoPhong, SoNguoiToiDa, DonGiaNgay, TrangThai, MaKhuVuc FROM Phong");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("SoPhong"),
                        rs.getInt("SoNguoiToiDa") + " người",
                        String.format("%,.0f VNĐ", rs.getDouble("DonGiaNgay")),
                        rs.getString("TrangThai"),
                        rs.getString("MaKhuVuc")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối cơ sở dữ liệu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi truy vấn SQL: " + ex.getMessage());
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    // 3. ĐẶT / NHẬN PHÒNG
    private void moFormDatNhanPhong() {
        JDialog dialog = new JDialog(this, "Danh Sách Đặt / Nhận Phòng", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Số Phiếu", "Ngày Lập", "Kênh Đặt", "Tiền Cọc", "Trạng Thái", "Mã Khách"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PhieuDatPhong");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("SoPhieuDat"),
                        rs.getDate("NgayLap"),
                        rs.getString("KenhDat"),
                        String.format("%,.0f VNĐ", rs.getDouble("TienCoc")),
                        rs.getString("TrangThai"),
                        rs.getString("MaKhach")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối cơ sở dữ liệu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi tải CSDL: " + ex.getMessage());
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    // 4. SỬ DỤNG DỊCH VỤ
    private void moFormSuDungDichVu() {
        JDialog dialog = new JDialog(this, "Danh Mục Dịch Vụ", true);
        dialog.setSize(650, 350);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Đơn Vị Tính", "Đơn Giá"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM DichVu");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaDV"),
                        rs.getString("TenDV"),
                        rs.getString("DonViTinh"),
                        String.format("%,.0f VNĐ", rs.getDouble("DonGia"))
                    });
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối cơ sở dữ liệu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi tải CSDL: " + ex.getMessage());
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    // 5. TRẢ PHÒNG & THANH TOÁN
    private void moFormTraPhongThanhToan() {
        JDialog dialog = new JDialog(this, "Thanh Toán & Hóa Đơn", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"Số Hóa Đơn", "Ngày Lập", "Tiền Phòng", "Tiền Dịch Vụ", "Tổng Tiền", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM HoaDon");
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("SoHoaDon"),
                        rs.getDate("NgayLap"),
                        String.format("%,.0f VNĐ", rs.getDouble("TienPhong")),
                        String.format("%,.0f VNĐ", rs.getDouble("TienDichVu")),
                        String.format("%,.0f VNĐ", rs.getDouble("TongTien")),
                        rs.getString("TrangThai")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối cơ sở dữ liệu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi tải CSDL: " + ex.getMessage());
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    // 6. THỐNG KÊ DOANH THU
    private void moFormThongKe() {
        JDialog dialog = new JDialog(this, "Báo Cáo Thống Kê Doanh Thu", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        double tongDoanhThu = 0;
        int tongHoaDon = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS SoHD, SUM(TongTien) AS TongTien FROM HoaDon");
                if (rs.next()) {
                    tongHoaDon = rs.getInt("SoHD");
                    tongDoanhThu = rs.getDouble("TongTien");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối cơ sở dữ liệu!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi tải CSDL: " + ex.getMessage());
        }

        JLabel lblInfo = new JLabel(String.format("<html><h2 style='text-align:center;'>BÁO CÁO DOANH THU CSDL</h2><p>• Tổng số hóa đơn: <b>%d</b></p><p>• Tổng doanh thu ghi nhận: <b style='color:green;'>%,.0f VNĐ</b></p></html>", tongHoaDon, tongDoanhThu), SwingConstants.CENTER);
        dialog.add(lblInfo, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyKhachSan app = new QuanLyKhachSan();
            app.setVisible(true);
        });
    }
}