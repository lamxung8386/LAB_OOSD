package vn.edu.benhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public final class DashboardPanel extends JPanel {
    private static final String[] LABELS = {
        "BỆNH NHÂN", "HỒ SƠ BỆNH ÁN", "LƯỢT KHÁM", "LỊCH HẸN CHỜ", "XÉT NGHIỆM CHỜ", "CHỜ THANH TOÁN"
    };
    private static final String[] KEYS = {
        "SoBenhNhan", "SoHoSo", "SoPhieuKham", "LichHenCho", "XetNghiemCho", "ChoThanhToan"
    };

    public DashboardPanel(Auth.User user) {
        setLayout(new BorderLayout(15,15));setBackground(Theme.BG);
        setBorder(new EmptyBorder(20,25,20,25));
        JPanel header=new JPanel(new GridLayout(2,1));header.setOpaque(false);
        header.add(Theme.title("Tổng quan hồ sơ bệnh án"));
        header.add(Theme.caption("Xin chào "+user.fullName()+" • Dữ liệu đọc trực tiếp từ SQL Server"));
        add(header,BorderLayout.NORTH);
        JPanel stats=new JPanel(new GridLayout(2,3,12,12));stats.setOpaque(false);
        int[] counts=new int[KEYS.length];
        try(Connection c=Db.open();Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT * FROM dbo.vw_ThongKe")){
            if(rs.next())for(int i=0;i<KEYS.length;i++)counts[i]=rs.getInt(KEYS[i]);
        }catch(Exception ex){Theme.error(this,ex);}
        for(int i=0;i<LABELS.length;i++){
            JPanel tile=Theme.card(new BorderLayout(0,6));
            JLabel desc=Theme.caption(LABELS[i]);
            JLabel value=new JLabel(String.valueOf(counts[i]));
            value.setFont(new Font("Segoe UI",Font.BOLD,33));value.setForeground(Theme.BLUE);
            tile.add(desc,BorderLayout.NORTH);tile.add(value,BorderLayout.CENTER);stats.add(tile);
        }
        JPanel main=new JPanel(new BorderLayout(12,12));main.setOpaque(false);
        main.add(stats,BorderLayout.NORTH);
        JPanel history=Theme.card(new BorderLayout(8,8));
        JLabel title=new JLabel("Hồ sơ bệnh án gần đây");title.setFont(new Font("Segoe UI",Font.BOLD,17));
        history.add(title,BorderLayout.NORTH);
        DefaultTableModel model=new DefaultTableModel(new String[]{"Mã hồ sơ","Mã BN","Bệnh nhân","Ngày lập","Số lần khám","Khám cuối"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        try(Connection c=Db.open();Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT TOP 60 MaHS,MaBN,BenhNhan,NgayLap,SoLanKham,LanKhamCuoi FROM dbo.vw_HoSoTongHop ORDER BY MaHS DESC")){
            while(rs.next()){
                Object[] row=new Object[6];for(int k=0;k<6;k++)row[k]=rs.getObject(k+1);model.addRow(row);
            }
        }catch(Exception ex){Theme.error(this,ex);}
        JTable table=new JTable(model);table.setRowHeight(30);table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        history.add(new JScrollPane(table),BorderLayout.CENTER);
        main.add(history,BorderLayout.CENTER);
        add(main,BorderLayout.CENTER);
    }
}
