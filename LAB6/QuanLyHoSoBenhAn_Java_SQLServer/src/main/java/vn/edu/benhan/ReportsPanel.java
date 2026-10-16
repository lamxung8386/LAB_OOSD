package vn.edu.benhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;

public final class ReportsPanel extends JPanel {
    private final JComboBox<String> reports = new JComboBox<>(new String[]{
        "Tổng hợp hồ sơ", "Lịch sử khám bệnh", "Lệ phí hàng năm", "Toa thuốc", "Thanh toán viện phí"
    });
    private final DefaultTableModel model=new DefaultTableModel(){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=new JTable(model);
    private final JLabel status=Theme.caption("Chọn báo cáo để xem thông tin");
    public ReportsPanel(){
        setLayout(new BorderLayout(12,12));setBackground(Theme.BG);
        setBorder(new EmptyBorder(20,25,20,25));
        JPanel heading=new JPanel(new BorderLayout(10,10));heading.setOpaque(false);
        heading.add(Theme.title("Tra cứu và thống kê"),BorderLayout.NORTH);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT,9,0));controls.setOpaque(false);
        reports.setPreferredSize(new Dimension(260,31));
        controls.add(reports);
        JButton refresh=Theme.button("Xem báo cáo"),export=Theme.button("Xuất CSV");
        controls.add(refresh);controls.add(export);controls.add(status);
        heading.add(controls,BorderLayout.SOUTH);add(heading,BorderLayout.NORTH);
        JPanel card=Theme.card(new BorderLayout());
        table.setRowHeight(30);table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        card.add(new JScrollPane(table),BorderLayout.CENTER);add(card,BorderLayout.CENTER);
        refresh.addActionListener(e->reload());
        export.addActionListener(e->exportCsv());
        reports.addActionListener(e->reload());
        reload();
    }

    private static final String[] QUERIES = {
        "SELECT * FROM dbo.vw_HoSoTongHop ORDER BY MaHS DESC",
        "SELECT pk.MaPK, bn.HoTen AS BenhNhan, bs.HoTen AS BacSi, pk.NgayGioKham, pk.TrieuChung, pk.ChanDoan, pk.LoaiKham, pk.TrangThai " +
           "FROM dbo.PhieuKham pk JOIN dbo.HoSoBenhAn hs ON pk.MaHS=hs.MaHS JOIN dbo.BenhNhan bn ON bn.MaBN=hs.MaBN " +
           "JOIN dbo.BacSi bs ON bs.MaBS=pk.MaBS ORDER BY pk.MaPK DESC",
        "SELECT phi.MaPhi,bn.HoTen AS BenhNhan,phi.Nam,phi.SoTien,phi.NgayDong,phi.GhiChu FROM dbo.DongPhiNam phi " +
           "JOIN dbo.BenhNhan bn ON bn.MaBN=phi.MaBN ORDER BY phi.MaPhi DESC",
        "SELECT t.MaToa,bn.HoTen AS BenhNhan,bs.HoTen AS BacSi,t.NgayKe,ct.TenThuoc,ct.LieuDung,ct.SoLuong,ct.CachDung " +
           "FROM dbo.ToaThuoc t JOIN dbo.PhieuKham pk ON pk.MaPK=t.MaPK JOIN dbo.HoSoBenhAn hs ON hs.MaHS=pk.MaHS " +
           "JOIN dbo.BenhNhan bn ON bn.MaBN=hs.MaBN JOIN dbo.BacSi bs ON bs.MaBS=pk.MaBS " +
           "LEFT JOIN dbo.ChiTietToaThuoc ct ON ct.MaToa=t.MaToa ORDER BY t.MaToa DESC",
        "SELECT tt.MaTT,pk.MaPK,bn.HoTen AS BenhNhan,tt.SoTien,tt.NgayThanhToan,tt.TrangThai " +
           "FROM dbo.ThanhToanVienPhi tt JOIN dbo.PhieuKham pk ON pk.MaPK=tt.MaPK " +
           "JOIN dbo.HoSoBenhAn hs ON hs.MaHS=pk.MaHS JOIN dbo.BenhNhan bn ON bn.MaBN=hs.MaBN " +
           "ORDER BY tt.MaTT DESC"
    };

    private void reload(){
        try(Connection c=Db.open();Statement s=c.createStatement();
            ResultSet rs=s.executeQuery(QUERIES[reports.getSelectedIndex()])){
            ResultSetMetaData md=rs.getMetaData();int n=md.getColumnCount();
            String[] headings=new String[n];for(int i=1;i<=n;i++)headings[i-1]=md.getColumnLabel(i);
            DefaultTableModel m=new DefaultTableModel(headings,0){
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            while(rs.next()){
                Object[] values=new Object[n];for(int i=1;i<=n;i++)values[i-1]=rs.getObject(i);
                m.addRow(values);
            }
            model.setDataVector(new Object[0][0],new Object[0]);
            table.setModel(m);
            for(int col=0;col<table.getColumnCount();col++)table.getColumnModel().getColumn(col).setPreferredWidth(170);
            status.setText(m.getRowCount()+" dòng kết quả");
        }catch(Exception ex){Theme.error(this,ex);}
    }

    private static String csv(String s){
        return "\""+s.replace("\"","\"\"").replace('\n',' ').replace('\r',' ')+"\"";
    }
    private void exportCsv(){
        JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new java.io.File("BaoCaoBenhAn.csv"));
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        int answer=JOptionPane.showConfirmDialog(this,
                "Báo cáo có thông tin sức khỏe. Chỉ xuất trên thiết bị được phép và giữ bí mật. Tiếp tục?",
                "Xác nhận xuất dữ liệu",JOptionPane.YES_NO_OPTION);
        if(answer!=JOptionPane.YES_OPTION)return;
        try{
            javax.swing.table.TableModel m=table.getModel();StringBuilder csv=new StringBuilder("\uFEFF");
            for(int i=0;i<m.getColumnCount();i++){if(i>0)csv.append(',');csv.append(csv(m.getColumnName(i)));}
            csv.append("\r\n");
            for(int r=0;r<m.getRowCount();r++){
                for(int c=0;c<m.getColumnCount();c++){
                    if(c>0)csv.append(',');Object v=m.getValueAt(r,c);
                    csv.append(csv(v==null?"":v.toString()));
                }
                csv.append("\r\n");
            }
            Files.writeString(chooser.getSelectedFile().toPath(),csv.toString(),StandardCharsets.UTF_8);
            Theme.info(this,"Đã xuất báo cáo CSV.");
        }catch(Exception ex){Theme.error(this,ex);}
    }
}
