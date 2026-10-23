import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

/** Giao diện desktop thuần Swing, dành cho bài thực hành Bài 7. */
public class GiaoDien extends JFrame {
    private static final Color BG=new Color(244,247,252), NAV=new Color(22,36,61), ACCENT=new Color(43,108,224), TEXT=new Color(33,47,67);
    private static final Font FONT=new Font("Segoe UI",Font.PLAIN,14);
    private final NghiepVu nv;
    private Model.NguoiDung nguoiDung;
    private JPanel root, sidebar, main, heading;
    private JLabel appTitle;
    private String manHinh="Tổng quan";
    private final Map<String,JButton> nutMenu=new LinkedHashMap<>();
    private static final String[] ROLES={"NHAN_SU","TRUONG_PHONG","CHAM_CONG","KE_TOAN","GIAM_DOC","NHAN_VIEN"};
    private static final Map<String,String> TEN_VAI_TRO=Map.of(
        "NHAN_SU","Nhân sự", "TRUONG_PHONG","Trưởng phòng", "CHAM_CONG","Ban chấm công",
        "KE_TOAN","Kế toán", "GIAM_DOC","Giám đốc", "NHAN_VIEN","Nhân viên");

    public GiaoDien(NghiepVu nv){
        super("QUẢN LÝ NHÂN SỰ - Công ty sơn Đông Á");
        this.nv=nv;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050,670));setSize(1280,760);setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        dangNhap();
    }
    private void dangNhap(){
        JPanel login=new JPanel(new GridBagLayout());login.setBackground(BG);
        JPanel box=new JPanel(new GridBagLayout());box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(224,229,237)),new EmptyBorder(34,44,36,44)));
        GridBagConstraints g=new GridBagConstraints();g.gridx=0;g.fill=GridBagConstraints.HORIZONTAL;g.weightx=1;g.insets=new Insets(5,0,5,0);
        JLabel t=new JLabel("ĐĂNG NHẬP QUẢN LÝ NHÂN SỰ");t.setFont(new Font("Segoe UI",Font.BOLD,21));t.setForeground(TEXT);g.gridy=0;box.add(t,g);
        JLabel sub=new JLabel("Công ty sơn Đông Á  |  Ứng dụng Java Swing");sub.setForeground(new Color(99,115,137));g.gridy=1;box.add(sub,g);
        JComboBox<String> account=new JComboBox<>(nv.db.nguoiDung.keySet().toArray(new String[0]));styleCombo(account);g.gridy=2;box.add(fieldLabel("Tài khoản"),g);g.gridy=3;box.add(account,g);
        JPasswordField pass=new JPasswordField("123456",23);styleInput(pass);g.gridy=4;box.add(fieldLabel("Mật khẩu"),g);g.gridy=5;box.add(pass,g);
        JLabel demo=new JLabel("Dữ liệu mẫu: chọn tài khoản bất kỳ, mật khẩu 123456");demo.setForeground(new Color(98,113,133));demo.setFont(new Font("Segoe UI",Font.ITALIC,12));g.gridy=6;box.add(demo,g);
        JButton btn=button("Đăng nhập",true);g.gridy=7;g.insets=new Insets(18,0,2,0);box.add(btn,g);
        JLabel err=new JLabel(" ");err.setForeground(new Color(194,41,44));g.gridy=8;g.insets=new Insets(3,0,0,0);box.add(err,g);
        Runnable check=()->{
            Model.NguoiDung nd=nv.dangNhap((String)account.getSelectedItem(),new String(pass.getPassword()));
            if(nd==null){err.setText("Sai tài khoản hoặc mật khẩu.");return;}
            nguoiDung=nd;manHinh="Tổng quan";taoGiaoDienChinh();
        };
        btn.addActionListener(e->check.run());pass.addActionListener(e->check.run());
        login.add(box);setContentPane(login);revalidate();repaint();
    }
    private void taoGiaoDienChinh(){
        root=new JPanel(new BorderLayout());root.setBackground(BG);
        sidebar=new JPanel();sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));sidebar.setBackground(NAV);sidebar.setPreferredSize(new Dimension(238,0));sidebar.setBorder(new EmptyBorder(21,13,14,13));
        JLabel logo=new JLabel("  ĐÔNG Á  /  HRM");logo.setAlignmentX(Component.LEFT_ALIGNMENT);logo.setFont(new Font("Segoe UI",Font.BOLD,22));logo.setForeground(Color.WHITE);
        sidebar.add(logo);sidebar.add(Box.createVerticalStrut(5));
        JLabel sideSub=new JLabel("   QUẢN LÝ NHÂN SỰ");sideSub.setForeground(new Color(149,166,194));sideSub.setFont(new Font("Segoe UI",Font.PLAIN,11));sideSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sideSub);sidebar.add(Box.createVerticalStrut(22));
        nutMenu.clear();
        List<String> ds=new ArrayList<>();ds.add("Tổng quan");
        switch(nguoiDung.vaiTro){
            case "NHAN_SU" -> ds.addAll(List.of("Phòng ban","Nhân viên","Tuyển dụng","Hợp đồng","Quyết định lương","Quyết định chức vụ","Hồ sơ bảo hiểm","Thưởng / kỷ luật"));
            case "TRUONG_PHONG" -> ds.addAll(List.of("Đánh giá thử việc","Phân công ca làm"));
            case "CHAM_CONG" -> ds.addAll(List.of("Bảng chấm công","Phiếu phụ trội"));
            case "KE_TOAN" -> ds.addAll(List.of("Bảng lương","Phiếu thu / chi"));
            case "GIAM_DOC" -> ds.add("Duyệt thưởng / phạt");
            case "NHAN_VIEN" -> ds.addAll(List.of("Hồ sơ của tôi","Lương của tôi"));
        }
        for(String item:ds){
            JButton b=new JButton(item);b.setFont(FONT);b.setHorizontalAlignment(SwingConstants.LEFT);b.setMaximumSize(new Dimension(220,42));b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setFocusPainted(false);b.setOpaque(true);b.setBackground(NAV);b.setForeground(new Color(207,218,234));b.setBorder(new EmptyBorder(8,14,8,8));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addActionListener(e->hienThi(item));sidebar.add(b);sidebar.add(Box.createVerticalStrut(4));nutMenu.put(item,b);
        }
        sidebar.add(Box.createVerticalGlue());
        JLabel who=new JLabel("  "+nguoiDung.taiKhoan+"  /  "+TEN_VAI_TRO.get(nguoiDung.vaiTro));who.setFont(new Font("Segoe UI",Font.PLAIN,12));who.setForeground(new Color(201,217,243));who.setAlignmentX(Component.LEFT_ALIGNMENT);sidebar.add(who);sidebar.add(Box.createVerticalStrut(10));
        JButton doiMK=new JButton("Đổi mật khẩu");sideButton(doiMK);doiMK.addActionListener(e->doiMatKhau());sidebar.add(doiMK);sidebar.add(Box.createVerticalStrut(6));
        JButton out=new JButton("Đăng xuất");sideButton(out);out.addActionListener(e->{nguoiDung=null;dangNhap();});sidebar.add(out);
        root.add(sidebar,BorderLayout.WEST);
        JPanel page=new JPanel(new BorderLayout());page.setBackground(BG);
        heading=new JPanel(new BorderLayout());heading.setBackground(Color.WHITE);heading.setBorder(new EmptyBorder(19,27,19,27));
        appTitle=new JLabel();appTitle.setFont(new Font("Segoe UI",Font.BOLD,24));appTitle.setForeground(TEXT);heading.add(appTitle,BorderLayout.WEST);
        JLabel badge=new JLabel("Công ty sơn Đông Á  •  "+TEN_VAI_TRO.get(nguoiDung.vaiTro));badge.setFont(new Font("Segoe UI",Font.PLAIN,12));badge.setForeground(new Color(101,115,136));heading.add(badge,BorderLayout.EAST);
        page.add(heading,BorderLayout.NORTH);
        main=new JPanel(new BorderLayout(0,14));main.setBackground(BG);main.setBorder(new EmptyBorder(22,26,22,26));
        page.add(main,BorderLayout.CENTER);root.add(page,BorderLayout.CENTER);setContentPane(root);
        hienThi(manHinh);
    }
    private void sideButton(JButton b){b.setAlignmentX(Component.LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(220,34));b.setHorizontalAlignment(SwingConstants.LEFT);b.setFont(new Font("Segoe UI",Font.PLAIN,12));b.setForeground(Color.WHITE);b.setBackground(new Color(50,68,96));b.setBorder(new EmptyBorder(7,14,7,7));b.setFocusPainted(false);}
    private void hienThi(String name){
        manHinh=name;appTitle.setText(name);
        for(var item:nutMenu.entrySet()){boolean selected=item.getKey().equals(name);item.getValue().setBackground(selected?ACCENT:NAV);item.getValue().setForeground(selected?Color.WHITE:new Color(207,218,234));}
        main.removeAll();
        try {switch(name){
            case "Tổng quan"->tongQuan();case "Phòng ban"->phongBan();case "Nhân viên"->nhanVien();
            case "Tuyển dụng"->tuyenDung();case "Đánh giá thử việc"->danhGia();case "Hợp đồng"->hopDong();
            case "Quyết định lương"->qdLuong();case "Quyết định chức vụ"->chucVu();case "Hồ sơ bảo hiểm"->baoHiem();
            case "Phân công ca làm"->phanCong();case "Bảng chấm công"->chamCong();case "Phiếu phụ trội"->phuTroi();
            case "Thưởng / kỷ luật"->thuongPhat(false);case "Duyệt thưởng / phạt"->thuongPhat(true);
            case "Phiếu thu / chi"->thuChi();case "Bảng lương"->bangLuong(false);case "Lương của tôi"->bangLuong(true);case "Hồ sơ của tôi"->hoSoCaNhan();
            default->tongQuan();
        }}catch(Exception ex){error(ex);}
        main.revalidate();main.repaint();
    }
    private void thucHien(Runnable fn){try{fn.run();hienThi(manHinh);}catch(Exception ex){error(ex);}}
    private void error(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Không thực hiện được",JOptionPane.ERROR_MESSAGE);}
    private void thongBao(String msg){JOptionPane.showMessageDialog(this,msg,"Thành công",JOptionPane.INFORMATION_MESSAGE);}
    private static JLabel fieldLabel(String s){JLabel l=new JLabel(s);l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(TEXT);return l;}
    private static void styleInput(JTextField f){f.setFont(FONT);f.setPreferredSize(new Dimension(300,36));f.setBorder(new CompoundBorder(new LineBorder(new Color(207,216,228)),new EmptyBorder(6,10,6,10)));}
    private static void styleCombo(JComboBox<?> c){c.setFont(FONT);c.setBackground(Color.WHITE);c.setPreferredSize(new Dimension(300,36));}
    private static JButton button(String s,boolean primary){JButton b=new JButton(s);b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setFocusPainted(false);b.setBackground(primary?ACCENT:Color.WHITE);b.setForeground(primary?Color.WHITE:TEXT);b.setOpaque(true);b.setBorder(new CompoundBorder(new LineBorder(primary?ACCENT:new Color(204,213,226)),new EmptyBorder(9,15,9,15)));return b;}
    private JPanel white(){JPanel p=new JPanel(new BorderLayout(0,12));p.setBackground(Color.WHITE);p.setBorder(new CompoundBorder(new LineBorder(new Color(226,232,241)),new EmptyBorder(16,16,16,16)));return p;}
    private JPanel toolbar(String help){JPanel p=new JPanel(new BorderLayout());p.setOpaque(false);JLabel lbl=new JLabel(help);lbl.setForeground(new Color(98,112,133));lbl.setFont(new Font("Segoe UI",Font.PLAIN,12));p.add(lbl,BorderLayout.WEST);return p;}
    private JPanel actions(JButton... bs){JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));p.setOpaque(false);for(JButton b:bs)p.add(b);return p;}
    private JButton action(String name,Runnable r){JButton b=button(name,false);b.addActionListener(e->r.run());return b;}
    private JButton primary(String name,Runnable r){JButton b=button(name,true);b.addActionListener(e->r.run());return b;}
    private void addWhole(String desc,JComponent panel,JButton...buttons){
        JPanel w=white();JPanel north=new JPanel(new BorderLayout(0,12));north.setOpaque(false);north.add(toolbar(desc),BorderLayout.NORTH);
        if(buttons.length>0) north.add(actions(buttons),BorderLayout.SOUTH);
        w.add(north,BorderLayout.NORTH);w.add(panel,BorderLayout.CENTER);main.add(w,BorderLayout.CENTER);
    }
    private static String money(double d){return String.format(new Locale("vi","VN"),"%,.0f",d)+" đ";}
    private static String name(Model.DuLieu d,String id){Model.NhanVien n=d.nhanVien.get(id);return n==null?id:n.hoTen;}
    private static String dept(Model.DuLieu d,String id){Model.PhongBan p=d.phongBan.get(id);return p==null?id:p.ten;}
    private static String vnState(String s){return switch(s){case "MOI"->"Mới";case "THU_VIEC"->"Thử việc";case "DAT_THU_VIEC"->"Đạt thử việc";case "CHINH_THUC"->"Chính thức";case "KET_THUC"->"Kết thúc";case "CHO_DUYET"->"Chờ duyệt";case "DA_DUYET"->"Đã duyệt";case "TU_CHOI"->"Từ chối";default->s;};}
    private String today(){return LocalDate.now().toString();}
    private static Object[][] data(List<Object[]> rows){return rows.toArray(new Object[0][]);}
    private JTable table(String[] columns,List<Object[]> rows){
        DefaultTableModel model=new DefaultTableModel(data(rows),columns){public boolean isCellEditable(int a,int b){return false;}};
        JTable t=new JTable(model){public Component prepareRenderer(javax.swing.table.TableCellRenderer r,int row,int col){
            Component c=super.prepareRenderer(r,row,col);
            if(!isCellSelected(row,col))c.setBackground(row%2==0?Color.WHITE:new Color(247,249,253));
            c.setForeground(TEXT);return c;
        }};
        t.setFont(FONT);t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));t.getTableHeader().setBackground(new Color(235,241,250));t.getTableHeader().setForeground(TEXT);
        t.setRowHeight(38);t.setShowGrid(false);t.setIntercellSpacing(new Dimension(0,0));t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(new Color(212,229,255));t.setAutoCreateRowSorter(true);t.setFillsViewportHeight(true);t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int c=0;c<columns.length;c++){int width=c==0?110:columns[c].contains("Tên")||columns[c].contains("Lý do")||columns[c].contains("Điều khoản")?240:150;t.getColumnModel().getColumn(c).setPreferredWidth(width);}
        return t;
    }
    private JComponent tab(JTable table){
        JPanel all=new JPanel(new BorderLayout(0,9));all.setOpaque(false);
        JTextField filter=new JTextField();styleInput(filter);filter.setPreferredSize(new Dimension(270,33));filter.putClientProperty("JTextField.placeholderText","Tìm kiếm...");
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));top.setOpaque(false);top.add(new JLabel("Tìm kiếm:"));top.add(filter);
        JButton csv=action("Xuất CSV",()->xuatCSV(table));top.add(csv);
        JButton print=action("In bảng",()->{try{table.print();}catch(Exception ex){error(ex);}});top.add(print);
        filter.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            private void go(){TableRowSorter<?> sorter=(TableRowSorter<?>)table.getRowSorter();sorter.setRowFilter(RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(filter.getText())));}
            public void insertUpdate(javax.swing.event.DocumentEvent e){go();}public void removeUpdate(javax.swing.event.DocumentEvent e){go();}public void changedUpdate(javax.swing.event.DocumentEvent e){go();}
        });
        all.add(top,BorderLayout.NORTH);all.add(new JScrollPane(table),BorderLayout.CENTER);return all;
    }
    private String selected(JTable t){int r=t.getSelectedRow();if(r<0)throw new IllegalArgumentException("Hãy chọn một dòng trong bảng trước");return String.valueOf(t.getValueAt(r,0));}
    private String[] employeeOptions(){return nv.db.nhanVien.values().stream().map(n->n.ma+" - "+n.hoTen).toArray(String[]::new);}
    private String[] deptOptions(){return nv.db.phongBan.values().stream().map(x->x.ma+" - "+x.ten).toArray(String[]::new);}
    private static String code(String v){return v==null?"":v.split(" - ",2)[0].trim();}
    private static String idOrFirst(String v){return code(v);}
    private void xuatCSV(JTable t){
        JFileChooser fc=new JFileChooser();fc.setSelectedFile(new File("DuLieu_"+manHinh.replace(' ','_').replace('/','_')+".csv"));
        if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(Writer w=Files.newBufferedWriter(fc.getSelectedFile().toPath(),StandardCharsets.UTF_8)){
            w.write('\ufeff');TableModel tm=t.getModel();
            for(int c=0;c<tm.getColumnCount();c++){if(c>0)w.write(',');w.write(csvCell(tm.getColumnName(c)));}w.write("\n");
            for(int r=0;r<tm.getRowCount();r++){for(int c=0;c<tm.getColumnCount();c++){if(c>0)w.write(',');w.write(csvCell(String.valueOf(tm.getValueAt(r,c))));}w.write("\n");}
            thongBao("Đã xuất: "+fc.getSelectedFile().getAbsolutePath());
        }catch(Exception ex){error(ex);}
    }
    private String csvCell(String s){return '"'+s.replace("\"","\"\"")+'"';}

    private static class FormField{
        String key,label,value;String[] options;
        FormField(String key,String label,String value,String[] options){this.key=key;this.label=label;this.value=value;this.options=options;}
    }
    private static FormField f(String k,String t,String v){return new FormField(k,t,v,null);}
    private static FormField c(String k,String t,String... opts){return new FormField(k,t,"",opts);}
    private static FormField cp(String k,String t,String current,String... opts){return new FormField(k,t,current,opts);}
    private Map<String,String> form(String title,FormField... fields){
        JPanel panel=new JPanel(new GridBagLayout());panel.setBackground(Color.WHITE);
        GridBagConstraints g=new GridBagConstraints();g.gridx=0;g.weightx=1;g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(4,8,4,8);
        Map<String,JComponent> controls=new LinkedHashMap<>();int i=0;
        for(FormField spec:fields){g.gridy=i++;panel.add(fieldLabel(spec.label),g);g.gridy=i++;
            if(spec.options==null){JTextField tf=new JTextField(spec.value,27);styleInput(tf);panel.add(tf,g);controls.put(spec.key,tf);}
            else{JComboBox<String> cb=new JComboBox<>(spec.options);styleCombo(cb);if(spec.value!=null&&!spec.value.isBlank()){for(int k=0;k<cb.getItemCount();k++){if(String.valueOf(cb.getItemAt(k)).startsWith(spec.value+" - ")||String.valueOf(cb.getItemAt(k)).equals(spec.value)){cb.setSelectedIndex(k);break;}}}panel.add(cb,g);controls.put(spec.key,cb);}
        }
        JScrollPane scroll=new JScrollPane(panel);scroll.setBorder(null);scroll.setPreferredSize(new Dimension(475,Math.min(550,fields.length*69+20)));scroll.getVerticalScrollBar().setUnitIncrement(18);
        int answer=JOptionPane.showConfirmDialog(this,scroll,title,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(answer!=JOptionPane.OK_OPTION)return null;
        Map<String,String> values=new HashMap<>();for(var e:controls.entrySet()){
            JComponent x=e.getValue();values.put(e.getKey(), x instanceof JComboBox<?> cb?String.valueOf(cb.getSelectedItem()):((JTextField)x).getText().trim());
        }return values;
    }
    private void doiMatKhau(){
        Map<String,String> m=form("Đổi mật khẩu",f("cu","Mật khẩu hiện tại",""),f("moi","Mật khẩu mới (>= 6 ký tự)",""));
        if(m!=null)thucHien(()->{nv.doiMatKhau(nguoiDung,m.get("cu"),m.get("moi"));thongBao("Đã đổi mật khẩu");});
    }
    private void tongQuan(){
        JPanel cards=new JPanel(new GridLayout(1,4,14,0));cards.setOpaque(false);
        cards.add(stat("Nhân viên",nv.db.nhanVien.size(),"Đang lưu trong hồ sơ"));
        cards.add(stat("Phòng ban",nv.db.phongBan.size(),"Bộ phận hoạt động"));
        cards.add(stat("Thử việc",(int)nv.db.nhanVien.values().stream().filter(x->x.trangThai.equals("THU_VIEC")).count(),"Cần đánh giá sau thử việc"));
        cards.add(stat("Chờ phê duyệt",(int)nv.db.thuongPhat.values().stream().filter(x->x.trangThai.equals("CHO_DUYET")).count(),"Quyết định thưởng / phạt"));
        main.add(cards,BorderLayout.NORTH);
        JPanel center=white();JPanel body=new JPanel();body.setOpaque(false);body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));
        JLabel h=new JLabel("QUY TRÌNH NGHIỆP VỤ");h.setFont(new Font("Segoe UI",Font.BOLD,17));h.setForeground(TEXT);body.add(h);body.add(Box.createVerticalStrut(17));
        for(String s:List.of("01   Nhân sự: tiếp nhận nhân viên, lập quyết định thử việc",
                             "02   Trưởng phòng: đánh giá kết quả thử việc",
                             "03   Nhân sự: ký hợp đồng, quyết định lương / chức vụ, bảo hiểm",
                             "04   Trưởng phòng và ban chấm công: phân công ca, ghi ngày công và giờ phụ trội",
                             "05   Nhân sự lập thưởng / kỷ luật → Giám đốc duyệt → Kế toán chi/thu",
                             "06   Kế toán: tính bảng lương theo công + phụ trội × 1,5 ± thưởng / phạt - bảo hiểm")){
            JLabel l=new JLabel(s);l.setFont(FONT);l.setForeground(new Color(62,78,102));l.setAlignmentX(Component.LEFT_ALIGNMENT);l.setBorder(new EmptyBorder(9,3,10,3));body.add(l);
        }
        JLabel line=new JLabel("Dữ liệu tự lưu tại: "+nv.moTaLuuTru());line.setForeground(new Color(113,126,142));line.setFont(new Font("Segoe UI",Font.ITALIC,12));line.setBorder(new EmptyBorder(25,3,0,0));body.add(line);
        center.add(body,BorderLayout.NORTH);main.add(center,BorderLayout.CENTER);
    }
    private JPanel stat(String label,int number,String desc){JPanel p=white();p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JLabel a=new JLabel(label);a.setForeground(new Color(98,112,133));a.setFont(FONT);a.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel b=new JLabel(""+number);b.setFont(new Font("Segoe UI",Font.BOLD,31));b.setForeground(ACCENT);b.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel c=new JLabel(desc);c.setFont(new Font("Segoe UI",Font.PLAIN,11));c.setForeground(new Color(115,131,150));c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(a);p.add(Box.createVerticalStrut(8));p.add(b);p.add(Box.createVerticalStrut(4));p.add(c);return p;
    }
    private void phongBan(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.phongBan.values())rows.add(new Object[]{x.ma,x.ten,x.truongPhong});
        JTable t=table(new String[]{"Mã phòng","Tên phòng ban","Trưởng phòng"},rows);
        JButton add=primary("+ Thêm phòng",()->{Map<String,String> m=form("Thêm phòng ban",f("id","Mã phòng","PB05"),f("name","Tên phòng ban",""),f("lead","Trưởng phòng",""));if(m!=null)thucHien(()->nv.themPhong(m.get("id"),m.get("name"),m.get("lead")));});
        JButton edit=action("Sửa",()->{try{var x=nv.db.phongBan.get(selected(t));Map<String,String> m=form("Sửa phòng",f("name","Tên phòng ban",x.ten),f("lead","Trưởng phòng",x.truongPhong));if(m!=null)thucHien(()->nv.suaPhong(x.ma,m.get("name"),m.get("lead")));}catch(Exception ex){error(ex);}});
        JButton del=action("Xóa",()->{try{String id=selected(t);if(confirm("Xóa phòng "+id+"?"))thucHien(()->nv.xoaPhong(id));}catch(Exception ex){error(ex);}});
        addWhole("Quản lý danh mục phòng ban và trưởng phòng.",tab(t),add,edit,del);
    }
    private boolean confirm(String q){return JOptionPane.showConfirmDialog(this,q,"Xác nhận",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    private void nhanVien(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.nhanVien.values())rows.add(new Object[]{x.ma,x.hoTen,dept(nv.db,x.phongBan),x.ngayVao,x.sdt,vnState(x.trangThai)});
        JTable t=table(new String[]{"Mã NV","Họ tên","Phòng ban","Ngày vào","Điện thoại","Trạng thái"},rows);
        JButton add=primary("+ Thêm nhân viên",()->{Map<String,String> m=form("Nhân viên mới",f("id","Mã nhân viên","NV007"),f("name","Họ và tên",""),c("dept","Phòng ban",deptOptions()),f("date","Ngày vào (yyyy-MM-dd)",today()),f("phone","Số điện thoại",""));
            if(m!=null)thucHien(()->nv.themNhanVien(m.get("id"),m.get("name"),code(m.get("dept")),m.get("date"),m.get("phone")));});
        JButton edit=action("Sửa",()->{try{var x=nv.db.nhanVien.get(selected(t));Map<String,String> m=form("Sửa thông tin nhân viên",f("name","Họ tên",x.hoTen),cp("dept","Phòng ban",x.phongBan,deptOptions()),f("phone","Số điện thoại",x.sdt));
            if(m!=null)thucHien(()->nv.suaNhanVien(x.ma,m.get("name"),code(m.get("dept")),m.get("phone")));}catch(Exception ex){error(ex);}});
        JButton del=action("Xóa",()->{try{String id=selected(t);if(confirm("Xóa nhân viên "+id+"?"))thucHien(()->nv.xoaNhanVien(id));}catch(Exception ex){error(ex);}});
        JButton detail=action("Xem hồ sơ",()->{try{showProfile(selected(t));}catch(Exception ex){error(ex);}});
        addWhole("Hồ sơ cơ bản, phòng ban và trạng thái làm việc.",tab(t),add,edit,del,detail);
    }
    private void showProfile(String id){var n=nv.db.nhanVien.get(id);if(n==null)throw new IllegalArgumentException("Không tồn tại nhân viên");
        StringBuilder s=new StringBuilder("Nhân viên: "+n.ma+" - "+n.hoTen+"\nPhòng ban: "+dept(nv.db,n.phongBan)+"\nNgày vào: "+n.ngayVao+"\nTrạng thái: "+vnState(n.trangThai)+"\n\n");
        nv.db.tuyenDung.values().stream().filter(x->x.nhanVien.equals(id)).forEach(x->s.append("Tuyển dụng: "+x.ma+" ("+x.batDau+" đến "+x.ketThuc+")\n"));
        nv.db.danhGia.values().stream().filter(x->x.nhanVien.equals(id)).forEach(x->s.append("Đánh giá: "+x.ketQua+" - "+x.nhanXet+"\n"));
        nv.db.hopDong.values().stream().filter(x->x.nhanVien.equals(id)).forEach(x->s.append("Hợp đồng: "+x.ma+" - "+x.loai+"\n"));
        nv.db.luong.values().stream().filter(x->x.nhanVien.equals(id)).forEach(x->s.append("Quyết định lương: "+x.ma+" - "+money(x.luongCoBan)+" (từ "+x.hieuLuc+")\n"));
        nv.db.chucVu.values().stream().filter(x->x.nhanVien.equals(id)).forEach(x->s.append("Chức vụ: "+x.chucVu+" - "+x.ma+"\n"));
        JTextArea area=new JTextArea(s.toString(),16,58);area.setEditable(false);area.setFont(FONT);area.setLineWrap(true);area.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this,new JScrollPane(area),"Hồ sơ nhân viên",JOptionPane.PLAIN_MESSAGE);
    }
    private void hoSoCaNhan(){String ma=nguoiDung.nhanVien;if(ma==null||ma.isBlank()){main.add(new JLabel("Tài khoản chưa gắn với nhân viên"));return;}
        JButton xem=primary("Xem toàn bộ hồ sơ",()->showProfile(ma));JPanel card=white();JLabel lbl=new JLabel("Hồ sơ của "+name(nv.db,ma)+" ("+ma+")");lbl.setFont(new Font("Segoe UI",Font.BOLD,18));lbl.setForeground(TEXT);
        card.add(lbl,BorderLayout.NORTH);card.add(actions(xem),BorderLayout.CENTER);main.add(card,BorderLayout.NORTH);}
    private void tuyenDung(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.tuyenDung.values())rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.batDau,x.ketThuc,money(x.luongThuViec),vnState(nv.db.nhanVien.get(x.nhanVien).trangThai)});
        JTable t=table(new String[]{"Mã QĐ","Mã NV","Tên nhân viên","Thử việc từ","Đến ngày","Lương thử việc","Trạng thái NV"},rows);
        JButton add=primary("+ Lập quyết định tuyển dụng",()->{Map<String,String> m=form("Tiếp nhận thử việc",c("nv","Nhân viên",employeeOptions()),f("ky","Ngày ký",today()),f("bd","Bắt đầu",today()),f("kt","Kết thúc",LocalDate.now().plusMonths(2).toString()),f("luong","Lương thử việc", "6500000"),f("dk","Điều khoản","Theo quy định của công ty"));
            if(m!=null)thucHien(()->{String id=nv.tuyenDung(code(m.get("nv")),m.get("ky"),m.get("bd"),m.get("kt"),NghiepVu.so(m.get("luong"),"Lương thử việc"),m.get("dk"));thongBao("Đã lập "+id);});});
        addWhole("Bắt đầu quy trình Câu 4–5: tạo quyết định và thời gian thử việc.",tab(t),add);
    }
    private void danhGia(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.tuyenDung.values()){
            String kq=nv.db.danhGia.values().stream().filter(d->d.nhanVien.equals(x.nhanVien)).map(d->d.ketQua).findFirst().orElse("CHƯA ĐÁNH GIÁ");
            rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.ketThuc,kq});}
        JTable t=table(new String[]{"Mã QĐ tuyển","Mã NV","Tên nhân viên","Kết thúc thử việc","Kết quả đánh giá"},rows);
        JButton add=primary("Đánh giá thử việc",()->{try{
            int r=t.getSelectedRow();if(r<0)throw new IllegalArgumentException("Chọn nhân viên thử việc để đánh giá");String id=String.valueOf(t.getValueAt(r,1));
            Map<String,String> m=form("Đánh giá nhân viên "+id,c("kq","Kết quả","DAT","KHONG_DAT"),f("ngay","Ngày đánh giá",today()),f("note","Nhận xét","Đã hoàn thành thử việc"));
            if(m!=null)thucHien(()->{nv.danhGia(id,m.get("ngay"),m.get("kq").equals("DAT"),m.get("note"));thongBao("Đã lưu đánh giá cho "+id);});
        }catch(Exception ex){error(ex);}});
        addWhole("Trưởng phòng đánh giá khi đã hết thời gian thử việc. Đạt → ký hợp đồng; không đạt → kết thúc.",tab(t),add);
    }
    private void hopDong(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.hopDong.values())rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.loai,x.ngayKy,x.batDau,x.ketThuc});
        JTable t=table(new String[]{"Mã HĐ","Mã NV","Họ tên","Loại","Ngày ký","Hiệu lực","Kết thúc"},rows);
        JButton add=primary("+ Ký hợp đồng",()->{
            Map<String,String> m=form("Ký hợp đồng chính thức",c("nv","Nhân viên",employeeOptions()),c("loai","Loại hợp đồng","NGAN_HAN","DAI_HAN"),f("ky","Ngày ký",today()),f("bd","Bắt đầu",today()),f("kt","Kết thúc (trống nếu dài hạn)",LocalDate.now().plusYears(1).toString()),f("xh","Số BHXH (nếu dài hạn)",""),f("yt","Số BHYT (nếu dài hạn)",""),f("dong","Trích BH hàng tháng (VND)","0"));
            if(m!=null)thucHien(()->{String id=nv.hopDong(code(m.get("nv")),m.get("loai"),m.get("ky"),m.get("bd"),m.get("kt"),m.get("xh"),m.get("yt"),NghiepVu.so(m.get("dong"),"Mức đóng"));thongBao("Đã tạo hợp đồng "+id+". Hãy lập thêm quyết định lương.");});
        });
        addWhole("Chỉ ký sau khi đánh giá ĐẠT; hợp đồng dài hạn tự tạo hồ sơ bảo hiểm.",tab(t),add);
    }
    private void qdLuong(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.luong.values())rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.hieuLuc,money(x.luongCoBan),money(x.phuCap),money(x.docHai)});
        JTable t=table(new String[]{"Mã QĐ","Mã NV","Họ tên","Hiệu lực","Lương cơ bản","Phụ cấp lương","Độc hại"},rows);
        JButton add=primary("+ Quyết định lương",()->{Map<String,String> m=form("Lập quyết định lương",c("nv","Nhân viên",employeeOptions()),f("date","Hiệu lực",today()),f("cb","Lương cơ bản", "10000000"),f("pc","Phụ cấp lương","500000"),f("dh","Phụ cấp độc hại","0"));
            if(m!=null)thucHien(()->nv.quyetDinhLuong(code(m.get("nv")),m.get("date"),NghiepVu.so(m.get("cb"),"Lương cơ bản"),NghiepVu.so(m.get("pc"),"Phụ cấp"),NghiepVu.so(m.get("dh"),"Độc hại")));});
        addWhole("Lưu lịch sử thay đổi lương và ngày hiệu lực; bảng lương dùng quyết định mới nhất có hiệu lực.",tab(t),add);
    }
    private void chucVu(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.chucVu.values())rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.chucVu,x.hieuLuc,money(x.phuCap)});
        JTable t=table(new String[]{"Mã QĐ","Mã NV","Họ tên","Chức vụ","Hiệu lực","Phụ cấp chức vụ"},rows);
        JButton add=primary("+ Quyết định chức vụ",()->{Map<String,String> m=form("Bổ nhiệm / thay đổi chức vụ",c("nv","Nhân viên",employeeOptions()),f("cv","Chức vụ","Nhân viên"),f("date","Hiệu lực",today()),f("pc","Phụ cấp chức vụ","0"));
            if(m!=null)thucHien(()->nv.quyetDinhChucVu(code(m.get("nv")),m.get("date"),m.get("cv"),NghiepVu.so(m.get("pc"),"Phụ cấp")));});
        addWhole("Các quyết định bổ nhiệm và phụ cấp tương ứng.",tab(t),add);
    }
    private void baoHiem(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.baoHiem.values()){
            var hd=nv.db.hopDong.get(x.hopDong);rows.add(new Object[]{x.ma,x.hopDong,hd==null?"":hd.nhanVien,hd==null?"":name(nv.db,hd.nhanVien),x.bhxh,x.bhyt,money(x.mucDongThang)});}
        JTable t=table(new String[]{"Mã hồ sơ","Mã HĐ","Mã NV","Họ tên","Số BHXH","Số BHYT","Trích hàng tháng"},rows);
        addWhole("Hồ sơ BHXH/BHYT được tạo khi ký hợp đồng dài hạn.",tab(t));
    }
    private void phanCong(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.phanCong.values())rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.ngay,x.ca,x.soGio});
        JTable t=table(new String[]{"Mã phân công","Mã NV","Họ tên","Ngày","Ca làm","Số giờ"},rows);
        JButton add=primary("+ Phân công ca",()->{Map<String,String> m=form("Kế hoạch ca làm việc",c("nv","Nhân viên",employeeOptions()),f("ngay","Ngày làm",today()),c("ca","Ca làm","Ca sáng","Ca chiều","Ca đêm"),f("gio","Số giờ dự kiến","8"));
            if(m!=null)thucHien(()->nv.phanCong(code(m.get("nv")),m.get("ngay"),m.get("ca"),NghiepVu.so(m.get("gio"),"Số giờ")));});
        addWhole("Trưởng phòng lập kế hoạch phân công để ban chấm công theo dõi.",tab(t),add);
    }
    private void chamCong(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.chamCong.values())rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.thang,x.nam,x.ngayCong,x.gioLam});
        JTable t=table(new String[]{"Mã chấm công","Mã NV","Họ tên","Tháng","Năm","Ngày công","Giờ làm"},rows);
        JButton add=primary("+ Chấm công / cập nhật",()->{
            Map<String,String> m=form("Bảng chấm công",c("nv","Nhân viên",employeeOptions()),f("t","Tháng",""+LocalDate.now().getMonthValue()),f("n","Năm",""+LocalDate.now().getYear()),f("ngay","Số ngày công","24"),f("gio","Số giờ làm","192"));
            if(m!=null)thucHien(()->nv.chamCong(code(m.get("nv")),Integer.parseInt(m.get("t")),Integer.parseInt(m.get("n")),NghiepVu.so(m.get("ngay"),"Ngày công"),NghiepVu.so(m.get("gio"),"Giờ làm")));});
        addWhole("Một bản ghi mỗi nhân viên/tháng. Nhập lại cùng kỳ sẽ cập nhật ngày công và giờ làm.",tab(t),add);
    }
    private void phuTroi(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.phuTroi.values())rows.add(new Object[]{x.ma,x.ngay,x.hinhThuc});
        JTable t=table(new String[]{"Số phiếu","Ngày phụ trội","Hình thức"},rows);
        List<Object[]> det=new ArrayList<>();for(var x:nv.db.chiTietPhuTroi.values())det.add(new Object[]{x.ma,x.phieu,x.nhanVien,name(nv.db,x.nhanVien),x.gio,"1.5"});
        JTable d=table(new String[]{"Mã chi tiết","Số phiếu","Mã NV","Tên NV","Giờ","Hệ số"},det);
        JPanel two=new JPanel(new GridLayout(2,1,0,14));two.setOpaque(false);
        JPanel a=white();a.add(new JLabel("PHIẾU PHỤ TRỘI"),BorderLayout.NORTH);a.add(tab(t),BorderLayout.CENTER);two.add(a);
        JPanel b=white();b.add(new JLabel("CHI TIẾT NHÂN VIÊN THAM GIA"),BorderLayout.NORTH);b.add(tab(d),BorderLayout.CENTER);two.add(b);
        JButton add=primary("+ Lập phiếu phụ trội",()->{Map<String,String> m=form("Phiếu phụ trội",f("ngay","Ngày",today()),c("hinh","Hình thức","TANG_CA","NGAY_LE","NGAY_NGHI"));
            if(m!=null)thucHien(()->{String id=nv.phieuPhuTroi(m.get("ngay"),m.get("hinh"));thongBao("Phiếu "+id+" đã tạo. Tiếp tục thêm chi tiết nhân viên.");});});
        JButton detail=action("+ Thêm nhân viên phụ trội",()->{
            String[] ids=nv.db.phuTroi.values().stream().map(x->x.ma+" - "+x.ngay).toArray(String[]::new);
            if(ids.length==0){error(new IllegalArgumentException("Hãy lập phiếu phụ trội trước"));return;}
            Map<String,String> m=form("Chi tiết phiếu phụ trội",c("phieu","Phiếu phụ trội",ids),c("nv","Nhân viên",employeeOptions()),f("gio","Số giờ phụ trội","3"));
            if(m!=null)thucHien(()->nv.chiTietPhuTroi(code(m.get("phieu")),code(m.get("nv")),NghiepVu.so(m.get("gio"),"Số giờ")));
        });
        JPanel n=new JPanel(new BorderLayout());n.setOpaque(false);n.add(actions(add,detail),BorderLayout.WEST);
        main.add(n,BorderLayout.NORTH);main.add(two,BorderLayout.CENTER);
    }
    private void thuongPhat(boolean duyet){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.thuongPhat.values())rows.add(new Object[]{x.ma,x.loai,x.doiTuong,x.maDoiTuong,x.lyDo,money(x.soTien),x.hinhThuc,vnState(x.trangThai),x.nguoiKy});
        JTable t=table(new String[]{"Mã QĐ","Loại","Đối tượng","Mã đối tượng","Lý do","Số tiền","Chi trả","Trạng thái","Người ký"},rows);
        JButton btn;
        if(duyet){btn=primary("Duyệt quyết định",()->xacNhanQD(t,true));
            JButton reject=action("Từ chối",()->xacNhanQD(t,false));addWhole("Giám đốc duyệt và ký quyết định khen thưởng/kỷ luật.",tab(t),btn,reject);}
        else{btn=primary("+ Lập quyết định",()->{
            String[] doiTuong=java.util.stream.Stream.concat(Arrays.stream(employeeOptions()),Arrays.stream(deptOptions())).toArray(String[]::new);
            Map<String,String> m=form("Lập quyết định thưởng / kỷ luật",c("loai","Loại quyết định","THUONG","KY_LUAT"),c("dt","Cá nhân / phòng ban",doiTuong),f("ngay","Ngày quyết định",today()),f("lydo","Lý do","Hoàn thành nhiệm vụ"),f("tien","Số tiền","500000"),c("hinh","Thanh toán","TIEN_MAT","QUA_LUONG"));
            if(m!=null)thucHien(()->{String ma=code(m.get("dt")),loaiDt=ma.startsWith("PB")?"PHONG_BAN":"CA_NHAN";
                String id=nv.lapThuongPhat(m.get("loai"),loaiDt,ma,m.get("ngay"),m.get("lydo"),NghiepVu.so(m.get("tien"),"Số tiền"),m.get("hinh"));thongBao("Đã lập "+id+", đang chờ giám đốc duyệt.");});
        });addWhole("Nhân sự lập quyết định. Giám đốc phải duyệt trước khi kế toán chi hoặc đưa vào lương.",tab(t),btn);}
    }
    private void xacNhanQD(JTable t,boolean ok){try{String ma=selected(t);
        if(confirm((ok?"Duyệt và ký ":"Từ chối ")+ma+"?"))thucHien(()->{nv.duyet(ma,ok,"Giám đốc Công ty sơn Đông Á");thongBao("Đã xử lý "+ma);});
    }catch(Exception ex){error(ex);}}
    private void thuChi(){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.thuChi.values())rows.add(new Object[]{x.ma,x.quyetDinh,x.loai,x.ngay,money(x.soTien),x.ghiChu});
        JTable t=table(new String[]{"Mã phiếu","Mã quyết định","Loại phiếu","Ngày lập","Số tiền","Ghi chú"},rows);
        JButton add=primary("+ Lập phiếu thu / chi",()->{
            String[] ids=nv.db.thuongPhat.values().stream().filter(q->q.trangThai.equals("DA_DUYET")&&q.hinhThuc.equals("TIEN_MAT"))
                .filter(q->nv.db.thuChi.values().stream().noneMatch(x->x.quyetDinh.equals(q.ma)))
                .map(q->q.ma+" - "+q.loai+" / "+money(q.soTien)).toArray(String[]::new);
            if(ids.length==0){error(new IllegalArgumentException("Chưa có quyết định tiền mặt đã duyệt và chưa chi/thu"));return;}
            Map<String,String> m=form("Phiếu thu / chi tiền thưởng, tiền phạt",c("qd","Quyết định đã duyệt",ids),f("ngay","Ngày lập phiếu",today()),f("note","Ghi chú","Đã thanh toán"));
            if(m!=null)thucHien(()->{String id=nv.phieuThuChi(code(m.get("qd")),m.get("ngay"),m.get("note"));thongBao("Đã lập phiếu "+id);});
        });
        addWhole("Kế toán lập phiếu theo quyết định đã được giám đốc ký (không lập hai lần).",tab(t),add);
    }
    private void bangLuong(boolean own){
        List<Object[]> rows=new ArrayList<>();for(var x:nv.db.bangLuong.values()){
            if(own&&!x.nhanVien.equals(nguoiDung.nhanVien))continue;
            rows.add(new Object[]{x.ma,x.nhanVien,name(nv.db,x.nhanVien),x.thang+"/"+x.nam,money(x.luongCong),money(x.phuCap),money(x.tienPhuTroi),money(x.thuong),money(x.phat),money(x.baoHiem),money(x.thucLinh)});
        }
        JTable t=table(new String[]{"Mã bảng lương","Mã NV","Họ tên","Tháng","Lương theo công","Phụ cấp","Phụ trội","Thưởng","Phạt","Trích BH","Thực lĩnh"},rows);
        JLabel note=new JLabel("Công thức demo: lương cơ bản / 26 × ngày công + phụ cấp + giờ phụ trội × (lương cơ bản / 26 / 8) × 1,5 + thưởng - phạt - BH.");
        note.setFont(new Font("Segoe UI",Font.ITALIC,12));note.setForeground(new Color(93,108,127));
        if(own){addWhole("Lịch sử bảng lương cá nhân.",tab(t));main.add(note,BorderLayout.SOUTH);return;}
        JButton calc=primary("Tính và lưu lương",()->{Map<String,String> m=form("Tính bảng lương",c("nv","Nhân viên",employeeOptions()),f("t","Tháng",""+LocalDate.now().getMonthValue()),f("n","Năm",""+LocalDate.now().getYear()));
            if(m!=null)thucHien(()->{Model.BangLuong b=nv.luuBangLuong(code(m.get("nv")),Integer.parseInt(m.get("t")),Integer.parseInt(m.get("n")));thongBao("Thực lĩnh "+name(nv.db,b.nhanVien)+": "+money(b.thucLinh));});
        });
        JButton all=action("Tính cho tất cả",()->{
            Map<String,String> m=form("Tổng hợp lương",f("t","Tháng",""+LocalDate.now().getMonthValue()),f("n","Năm",""+LocalDate.now().getYear()));
            if(m==null)return;
            thucHien(()->{
                int thangTinh=Integer.parseInt(m.get("t")),namTinh=Integer.parseInt(m.get("n"));
                List<Model.BangLuong> ketQua=new ArrayList<>();StringBuilder boQua=new StringBuilder();
                for(String id:nv.db.nhanVien.keySet()){
                    try{ketQua.add(nv.tinhLuong(id,thangTinh,namTinh));}catch(Exception e){boQua.append(id).append(": ").append(e.getMessage()).append("\n");}
                }
                for(Model.BangLuong b:ketQua)nv.db.bangLuong.put(b.ma,b);nv.luu();
                thongBao("Đã tính "+ketQua.size()+" bảng lương.\nBỏ qua:\n"+(boQua.length()==0?"Không có":boQua));
            });
        });
        addWhole("Bảng lương hàng tháng dựa vào quyết định lương, chấm công, phụ trội và thưởng/phạt đã duyệt.",tab(t),calc,all);
        main.add(note,BorderLayout.SOUTH);
    }
}
