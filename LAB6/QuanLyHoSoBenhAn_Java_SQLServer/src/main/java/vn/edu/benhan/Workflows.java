package vn.edu.benhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

/** Two multi-table operations that must commit or roll back as one unit. */
public final class Workflows {
    private Workflows() {}

    public static JPanel registration(Auth.User user) {
        JPanel outer = new JPanel(new BorderLayout(15, 15)); outer.setBackground(Theme.BG);
        outer.setBorder(new EmptyBorder(20,25,20,25));
        outer.add(Theme.title("Đăng ký lần đầu + lập hồ sơ bệnh án"),BorderLayout.NORTH);
        JPanel card=Theme.card(new GridBagLayout());
        GridBagConstraints g=new GridBagConstraints(); g.gridx=0;g.weightx=1;
        g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(9,6,9,6);
        JComboBox<DataPanel.Option> patient=new JComboBox<>();
        JTextField fee=new JTextField("500000");
        JTextField note=new JTextField("Đăng ký theo dõi sức khỏe hàng năm");
        int y=0;
        addRow(card,g,y++,"Bệnh nhân (phải tạo ở mục Bệnh nhân trước)",patient);
        addRow(card,g,y++,"Lệ phí quản lý 1 năm (VNĐ)",fee);
        addRow(card,g,y++,"Ghi chú",note);
        JButton refresh=Theme.button("Tải danh sách bệnh nhân");
        JButton save=Theme.button("Lập hồ sơ và thu phí");
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0)); actions.setOpaque(false);
        actions.add(refresh);actions.add(save);g.gridy=y;card.add(actions,g);
        save.setEnabled(Auth.canEdit(user,"DANGKY"));
        refresh.addActionListener(e->fillCombo(patient,"BenhNhan","MaBN","HoTen",card));
        save.addActionListener(e->{
            try {
                DataPanel.Option selected=(DataPanel.Option)patient.getSelectedItem();
                if(selected==null||selected.id()==null)throw new IllegalArgumentException("Hãy chọn bệnh nhân.");
                BigDecimal amount=new BigDecimal(fee.getText().trim());
                if(amount.signum()<=0)throw new IllegalArgumentException("Lệ phí đăng ký phải lớn hơn 0.");
                Db.withTransaction(c->{
                    try(PreparedStatement check=c.prepareStatement("SELECT COUNT(*) FROM dbo.HoSoBenhAn WHERE MaBN=?")){
                        check.setInt(1,selected.id());try(ResultSet rs=check.executeQuery()){
                            rs.next();if(rs.getInt(1)>0)throw new SQLException("Bệnh nhân đã có hồ sơ; hãy dùng mục Gia hạn phí năm.");
                        }
                    }
                    int registrationId;
                    try(PreparedStatement insert=c.prepareStatement(
                            "INSERT dbo.PhieuDangKy(MaBN,NgayDangKy,GhiChu) VALUES(?,CAST(GETDATE() AS DATE),?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                        insert.setInt(1,selected.id());insert.setString(2,note.getText().trim());insert.executeUpdate();
                        try(ResultSet rs=insert.getGeneratedKeys()){
                            if(!rs.next())throw new SQLException("Không lấy được mã đăng ký.");
                            registrationId=rs.getInt(1);
                        }
                    }
                    try(PreparedStatement pay=c.prepareStatement(
                            "INSERT dbo.DongPhiNam(MaBN,Nam,SoTien,NgayDong,GhiChu) VALUES(?,?,?,CAST(GETDATE() AS DATE),?)")){
                        pay.setInt(1,selected.id());pay.setInt(2,LocalDate.now().getYear());
                        pay.setBigDecimal(3,amount);pay.setString(4,"Phí năm đăng ký lần đầu");pay.executeUpdate();
                    }
                    try(PreparedStatement dossier=c.prepareStatement(
                            "INSERT dbo.HoSoBenhAn(MaBN,MaDK,NgayLap,GhiChu) VALUES(?,?,CAST(GETDATE() AS DATE),?)")){
                        dossier.setInt(1,selected.id());dossier.setInt(2,registrationId);
                        dossier.setString(3,"Hồ sơ theo dõi sức khỏe và điều trị");dossier.executeUpdate();
                    }
                });
                Theme.info(card,"Đã tạo phiếu đăng ký, đóng phí năm và cấp hồ sơ bệnh án.\nMở mục Hồ sơ bệnh án để xem mã HS.");
                fillCombo(patient,"BenhNhan","MaBN","HoTen",card);
            }catch(Exception ex){Theme.error(card,ex);}
        });
        fillCombo(patient,"BenhNhan","MaBN","HoTen",card);
        JPanel wrap=new JPanel(new BorderLayout());wrap.setOpaque(false);wrap.add(card,BorderLayout.NORTH);
        JTextArea hint=new JTextArea("Quy trình theo đề:\n1. Tiếp nhận và lưu thông tin bệnh nhân (kèm đường dẫn ảnh 3x4).\n2. Lập phiếu đăng ký khám theo dõi sức khỏe.\n3. Thu lệ phí năm.\n4. Cấp mã hồ sơ bệnh án.\n\nNếu bất kỳ thao tác SQL nào lỗi, cả ba bước ghi dữ liệu đều được hoàn tác.");
        hint.setEditable(false);hint.setFont(new Font("Segoe UI",Font.PLAIN,13));
        hint.setBackground(Theme.BG);hint.setBorder(new EmptyBorder(20,4,0,4));
        wrap.add(hint,BorderLayout.CENTER);outer.add(wrap,BorderLayout.CENTER);
        return outer;
    }

    public static JPanel discharge(Auth.User user) {
        JPanel outer=new JPanel(new BorderLayout(15,15));outer.setBackground(Theme.BG);
        outer.setBorder(new EmptyBorder(20,25,20,25));
        outer.add(Theme.title("Thanh toán viện phí & cập nhật hồ sơ"),BorderLayout.NORTH);
        JPanel card=Theme.card(new GridBagLayout());
        GridBagConstraints g=new GridBagConstraints();g.gridx=0;g.weightx=1;
        g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(9,6,9,6);
        JComboBox<DataPanel.Option> visits=new JComboBox<>();
        JTextField amount=new JTextField("100000");
        JTextField note=new JTextField("Đã thanh toán viện phí / thủ tục xuất viện");
        addRow(card,g,0,"Phiếu khám chờ thanh toán",visits);
        addRow(card,g,1,"Số tiền thu (0 nếu khám định kỳ quý)",amount);
        addRow(card,g,2,"Ghi chú",note);
        JButton reload=Theme.button("Tải phiếu khám chờ"),save=Theme.button("Thanh toán & lưu bệnh án");
        save.setEnabled(Auth.canEdit(user,"THANHTOAN"));
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));actions.setOpaque(false);actions.add(reload);actions.add(save);
        g.gridy=3;card.add(actions,g);
        reload.addActionListener(e->loadVisits(visits,card));
        visits.addActionListener(e->{
            DataPanel.Option o=(DataPanel.Option)visits.getSelectedItem();
            if(o==null||o.id()==null)return;
            try(Connection c=Db.open();PreparedStatement p=c.prepareStatement(
                    "SELECT LoaiKham FROM dbo.PhieuKham WHERE MaPK=?")) {
                p.setInt(1,o.id());try(ResultSet rs=p.executeQuery()){
                    if(rs.next()&&"Định kỳ quý".equals(rs.getString(1)))amount.setText("0");
                    else amount.setText("100000");
                }
            }catch(Exception ex){Theme.error(card,ex);}
        });
        save.addActionListener(e->{
            try{
                DataPanel.Option v=(DataPanel.Option)visits.getSelectedItem();
                if(v==null||v.id()==null)throw new IllegalArgumentException("Chưa chọn phiếu khám.");
                BigDecimal sum=new BigDecimal(amount.getText().trim());
                if(sum.signum()<0)throw new IllegalArgumentException("Số tiền phải >= 0.");
                Db.withTransaction(c->{
                    String type,status;int patientId, visitYear, visitQuarter;
                    try(PreparedStatement p=c.prepareStatement(
                            "SELECT pk.LoaiKham,pk.TrangThai,hs.MaBN,YEAR(pk.NgayGioKham),DATEPART(QUARTER,pk.NgayGioKham) FROM dbo.PhieuKham pk WITH (UPDLOCK,ROWLOCK) " +
                            "JOIN dbo.HoSoBenhAn hs ON pk.MaHS=hs.MaHS WHERE pk.MaPK=?")) {
                        p.setInt(1,v.id());try(ResultSet rs=p.executeQuery()){
                            if(!rs.next())throw new SQLException("Không tìm thấy phiếu khám.");
                            type=rs.getString(1);status=rs.getString(2);patientId=rs.getInt(3);visitYear=rs.getInt(4);visitQuarter=rs.getInt(5);
                        }
                    }
                    if(!"Chờ thanh toán".equals(status))throw new SQLException("Chỉ xử lý phiếu có trạng thái Chờ thanh toán.");
                    if("Định kỳ quý".equals(type)){
                        try(PreparedStatement fee=c.prepareStatement(
                                "SELECT COUNT(*) FROM dbo.DongPhiNam WHERE MaBN=? AND Nam=?")){
                            fee.setInt(1,patientId);fee.setInt(2,visitYear);
                            try(ResultSet rs=fee.executeQuery()){
                                rs.next();if(rs.getInt(1)==0)throw new SQLException("Chưa đóng phí năm hiện tại, không áp dụng khám quý miễn phí.");
                            }
                        }
                        if(sum.signum()!=0)throw new SQLException("Khám định kỳ quý được miễn phí: hãy nhập 0.");
                        try(PreparedStatement once=c.prepareStatement(
                            "SELECT COUNT(*) FROM dbo.PhieuKham pk " +
                            "JOIN dbo.HoSoBenhAn hs ON hs.MaHS=pk.MaHS " +
                            "WHERE hs.MaBN=? AND pk.MaPK<>? AND pk.LoaiKham=N'Định kỳ quý' " +
                            "AND pk.TrangThai=N'Đã lưu' AND YEAR(pk.NgayGioKham)=? " +
                            "AND DATEPART(QUARTER,pk.NgayGioKham)=?")) {
                            once.setInt(1,patientId);once.setInt(2,v.id());
                            once.setInt(3,visitYear);once.setInt(4,visitQuarter);
                            try(ResultSet rs=once.executeQuery()){
                                rs.next();if(rs.getInt(1)>0)
                                    throw new SQLException("Bệnh nhân đã sử dụng suất khám định kỳ miễn phí trong quý này.");
                            }
                        }
                    }
                    try(PreparedStatement p=c.prepareStatement(
                            "INSERT dbo.ThanhToanVienPhi(MaPK,SoTien,TrangThai,GhiChu) VALUES(?,?,?,?)")) {
                        p.setInt(1,v.id());p.setBigDecimal(2,sum);
                        p.setString(3,sum.signum()==0?"Miễn phí":"Đã thanh toán");
                        p.setString(4,note.getText().trim());p.executeUpdate();
                    }
                    try(PreparedStatement p=c.prepareStatement(
                            "UPDATE dbo.PhieuKham SET TrangThai=N'Đã lưu',NgayLuuHoSo=SYSDATETIME() WHERE MaPK=?")) {
                        p.setInt(1,v.id());p.executeUpdate();
                    }
                });
                Theme.info(card,"Đã ghi nhận thanh toán và lưu đợt khám vào bệnh án.");loadVisits(visits,card);
            }catch(Exception ex){Theme.error(card,ex);}
        });
        loadVisits(visits,card);
        JPanel wrap=new JPanel(new BorderLayout());wrap.setOpaque(false);wrap.add(card,BorderLayout.NORTH);
        JTextArea hint=new JTextArea("LƯU Ý QUY TRÌNH\n• Bác sĩ nhập phiếu khám, chỉ định xét nghiệm và lập toa thuốc.\n• Khi hoàn tất khám, đổi trạng thái phiếu sang 'Chờ thanh toán'.\n• Phòng chăm sóc thu viện phí (hoặc xác nhận miễn phí khám định kỳ quý).\n• Phần mềm chỉ đánh dấu 'Đã lưu' khi thanh toán thành công.");
        hint.setEditable(false);hint.setFont(new Font("Segoe UI",Font.PLAIN,13));hint.setBackground(Theme.BG);
        hint.setBorder(new EmptyBorder(20,4,0,4));wrap.add(hint,BorderLayout.CENTER);
        outer.add(wrap,BorderLayout.CENTER);
        return outer;
    }

    private static void addRow(JPanel card,GridBagConstraints g,int y,String label,JComponent input){
        g.gridy=y*2;card.add(new JLabel(label),g);g.gridy=y*2+1;
        input.setPreferredSize(new Dimension(340,31));card.add(input,g);
    }

    private static void fillCombo(JComboBox<DataPanel.Option> combo,String table,String pk,String display,Component parent){
        combo.removeAllItems();combo.addItem(new DataPanel.Option(null,"-- Chọn bệnh nhân --"));
        try(Connection c=Db.open();Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT "+pk+","+display+" FROM dbo."+table+" ORDER BY "+pk)){
            while(rs.next())combo.addItem(new DataPanel.Option(rs.getInt(1),rs.getInt(1)+" - "+rs.getString(2)));
        }catch(Exception ex){Theme.error(parent,ex);}
    }

    private static void loadVisits(JComboBox<DataPanel.Option> combo,Component parent){
        combo.removeAllItems();combo.addItem(new DataPanel.Option(null,"-- Chọn phiếu khám --"));
        try(Connection c=Db.open();Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT pk.MaPK,bn.HoTen,pk.LoaiKham FROM dbo.PhieuKham pk " +
                    "JOIN dbo.HoSoBenhAn hs ON pk.MaHS=hs.MaHS " +
                    "JOIN dbo.BenhNhan bn ON bn.MaBN=hs.MaBN " +
                    "WHERE pk.TrangThai=N'Chờ thanh toán' ORDER BY pk.MaPK DESC")){
            while(rs.next())combo.addItem(new DataPanel.Option(rs.getInt(1),
                    "PK"+rs.getInt(1)+" | "+rs.getString(2)+" | "+rs.getString(3)));
        }catch(Exception ex){Theme.error(parent,ex);}
    }
}
