package vn.edu.benhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Application entry point. */
public final class Main {
    private Main() {}

    public static void main(String[] args){
        Theme.configure();
        SwingUtilities.invokeLater(Main::start);
    }

    private static void start(){
        try(Connection ignored=Db.open()){
            Auth.initDemoUsers();
        }catch(Exception e){
            int result=JOptionPane.showOptionDialog(null,
                "Không thể kết nối SQL Server.\n\n"+e.getMessage()+
                "\n\nKiểm tra SQL Server đang chạy, cổng TCP 1433, tài khoản sa,\nfile config.properties và đã chạy script 01_Tao_CSDL.sql.",
                "Lỗi kết nối SQL Server",JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,null,new String[]{"Thử lại", "Thoát"},"Thử lại");
            if(result==0)SwingUtilities.invokeLater(Main::start);
            return;
        }
        showLogin();
    }

    /** Login uses fixed-height inputs, so DPI scaling/Windows Look & Feel cannot squash them. */
    
    // GIAO DIEN DANG NHAP DA SUA
    private static void showLogin() {
        JDialog dialog = new JDialog(
                (Frame) null,
                "Đăng nhập - Quản lý hồ sơ bệnh án",
                true
        );

        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(Theme.WHITE);
        root.setBorder(new EmptyBorder(28, 36, 28, 36));

        // PHAN TIEU DE
        JPanel header = new JPanel(new GridLayout(4, 1, 0, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("HỆ THỐNG BỆNH ÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(Theme.NAVY);
        header.add(title);

        header.add(Theme.caption(
                "Đăng nhập để truy cập dữ liệu SQL Server"
        ));

        header.add(Theme.caption(
                "Tài khoản demo: admin / admin123"
        ));

        JLabel version = new JLabel(
                "GIAO DIỆN V3 - ĐÃ SỬA Ô NHẬP"
        );
        version.setForeground(new Color(30, 116, 90));
        version.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        header.add(version);

        root.add(header, BorderLayout.NORTH);

        // PHAN FORM DANG NHAP
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        // TEN DANG NHAP
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        g.gridy = 0;
        g.insets = new Insets(4, 0, 7, 0);
        form.add(usernameLabel, g);

        JTextField username = new JTextField("admin");
        username.setName("login_username_v3");

        JPanel usernameHolder = loginFieldHolder(username);

        g.gridy = 1;
        g.insets = new Insets(0, 0, 20, 0);
        form.add(usernameHolder, g);

        // MAT KHAU
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        g.gridy = 2;
        g.insets = new Insets(0, 0, 7, 0);
        form.add(passwordLabel, g);

        JPasswordField password = new JPasswordField();
        password.setName("login_password_v3");

        JPanel passwordHolder = loginFieldHolder(password);

        g.gridy = 3;
        g.insets = new Insets(0, 0, 12, 0);
        form.add(passwordHolder, g);

        // HIEN THI LOI DANG NHAP
        JLabel failure = new JLabel(" ");
        failure.setForeground(new Color(180, 44, 45));
        failure.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        g.gridy = 4;
        g.insets = new Insets(0, 0, 0, 0);
        form.add(failure, g);

        root.add(form, BorderLayout.CENTER);

        // NUT DANG NHAP
        JButton submit = Theme.button("ĐĂNG NHẬP");
        submit.setName("login_submit_v3");
        submit.setFont(
                new Font("Segoe UI", Font.BOLD, 16)
        );
        submit.setPreferredSize(new Dimension(450, 48));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(submit, BorderLayout.CENTER);

        root.add(footer, BorderLayout.SOUTH);

        // XU LY DANG NHAP SQL SERVER
        submit.addActionListener(e -> {
            try {
                Auth.User user = Auth.login(
                        username.getText(),
                        password.getPassword()
                );

                password.setText("");

                if (user == null) {
                    failure.setText(
                            "Sai tài khoản hoặc mật khẩu."
                    );
                    password.requestFocusInWindow();
                    return;
                }

                dialog.dispose();
                new MainWindow(user).setVisible(true);

            } catch (Exception ex) {
                Theme.error(dialog, ex);
            }
        });

        password.addActionListener(
                e -> submit.doClick()
        );

        dialog.getRootPane().setDefaultButton(submit);
        dialog.setContentPane(root);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(520, 490));
        dialog.setSize(
                Math.max(dialog.getWidth(), 560),
                Math.max(dialog.getHeight(), 510)
        );

        dialog.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(
                password::requestFocusInWindow
        );

        dialog.setVisible(true);
    }

    // GIU CHIEU CAO O NHAP KHONG BI CO LAI
    private static JPanel loginFieldHolder(JTextField field) {
        field.setFont(
                new Font("Segoe UI", Font.PLAIN, 16)
        );
        field.setForeground(Theme.TEXT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Theme.NAVY);
        field.setOpaque(true);

        field.setBorder(
                new javax.swing.border.CompoundBorder(
                        new javax.swing.border.LineBorder(
                                new Color(153, 171, 191),
                                1,
                                true
                        ),
                        new EmptyBorder(9, 12, 9, 12)
                )
        );

        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(field, BorderLayout.CENTER);

        holder.setPreferredSize(new Dimension(460, 46));
        holder.setMinimumSize(new Dimension(320, 46));

        return holder;
    }


    private static final class MainWindow extends JFrame {
        private final Auth.User user;
        private final JPanel display=new JPanel(new BorderLayout());
        private final JLabel current=new JLabel("Tổng quan");
        private final JPanel nav=new JPanel();
        private final Map<String,String> items=new LinkedHashMap<>();
        private String active;

        private MainWindow(Auth.User session){
            super("Quản lý hồ sơ bệnh án - Java Swing / SQL Server");
            user=session;setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(1060,690));setSize(1390,850);setLocationRelativeTo(null);
            items.put("HOME","▦  Tổng quan");
            items.put("BENHNHAN","▤  Bệnh nhân");
            items.put("DANGKY","✚  Đăng ký + lập hồ sơ");
            items.put("PHIEUDANGKY","▤  Phiếu đăng ký");
            items.put("DONGPHI","◉  Đóng phí hàng năm");
            items.put("HOSO","▣  Hồ sơ bệnh án");
            items.put("LICHHEN","▦  Lịch hẹn");
            items.put("KHOA","▤  Khoa điều trị");
            items.put("BACSI","♙  Bác sĩ");
            items.put("PHIEUKHAM","✚  Phiếu khám");
            items.put("XETNGHIEM","◇  Xét nghiệm");
            items.put("TOATHUOC","≡  Toa thuốc");
            items.put("CHITIETTOA","▪  Chi tiết toa thuốc");
            items.put("DISCHARGE","✓  Xuất viện + thanh toán");
            items.put("THANHTOAN","▤  Lịch sử thanh toán");
            items.put("REPORT","▥  Báo cáo - tra cứu");
            JPanel root=new JPanel(new BorderLayout());root.setBackground(Theme.BG);
            root.add(createSidebar(),BorderLayout.WEST);
            JPanel center=new JPanel(new BorderLayout());center.setBackground(Theme.BG);
            center.add(createToolbar(),BorderLayout.NORTH);
            display.setBackground(Theme.BG);center.add(display,BorderLayout.CENTER);
            root.add(center,BorderLayout.CENTER);setContentPane(root);
            openSection("HOME");
        }

        private JComponent createSidebar(){
            JPanel outer=new JPanel(new BorderLayout());outer.setBackground(Theme.NAVY);
            outer.setPreferredSize(new Dimension(238,650));
            JLabel title=new JLabel("  HỒ SƠ BỆNH ÁN");
            title.setFont(new Font("Segoe UI",Font.BOLD,18));title.setForeground(Color.WHITE);
            title.setBorder(new EmptyBorder(23,15,22,10));outer.add(title,BorderLayout.NORTH);
            nav.setLayout(new BoxLayout(nav,BoxLayout.Y_AXIS));nav.setBackground(Theme.NAVY);
            for(Map.Entry<String,String> item:items.entrySet()){
                String key=item.getKey();JButton b=new JButton(item.getValue());
                b.setName(key);b.setAlignmentX(Component.LEFT_ALIGNMENT);
                b.setMaximumSize(new Dimension(Integer.MAX_VALUE,37));
                b.setHorizontalAlignment(SwingConstants.LEFT);
                b.setBorder(new EmptyBorder(8,18,8,8));b.setFocusPainted(false);
                b.setFont(new Font("Segoe UI",Font.PLAIN,13));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                b.setForeground(Color.WHITE);b.setBackground(Theme.NAVY);b.setOpaque(true);
                b.addActionListener(e->openSection(key));nav.add(b);
            }
            JScrollPane sp=new JScrollPane(nav);sp.setBorder(null);
            sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            sp.getVerticalScrollBar().setUnitIncrement(18);
            outer.add(sp,BorderLayout.CENTER);
            JLabel foot=new JLabel("  JAVA SWING  •  SQL SERVER");foot.setForeground(new Color(173,200,227));
            foot.setFont(new Font("Segoe UI",Font.PLAIN,11));foot.setBorder(new EmptyBorder(13,8,16,8));
            outer.add(foot,BorderLayout.SOUTH);return outer;
        }

        private JComponent createToolbar(){
            JPanel toolbar=new JPanel(new BorderLayout());toolbar.setBackground(Color.WHITE);
            toolbar.setBorder(new EmptyBorder(13,22,13,21));
            current.setFont(new Font("Segoe UI",Font.BOLD,16));current.setForeground(Theme.NAVY);
            toolbar.add(current,BorderLayout.WEST);
            JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));right.setOpaque(false);
            JLabel who=Theme.caption(user.fullName()+"  ("+user.role()+")");right.add(who);
            JButton logout=Theme.button("Đăng xuất");logout.addActionListener(e->{dispose();showLogin();});
            right.add(logout);toolbar.add(right,BorderLayout.EAST);return toolbar;
        }

        private void openSection(String key){
            active=key;current.setText(items.getOrDefault(key,key));
            for(Component comp:nav.getComponents())if(comp instanceof JButton b){
                boolean chosen=key.equals(b.getName());
                b.setBackground(chosen?Theme.BLUE:Theme.NAVY);
                b.setFont(new Font("Segoe UI",chosen?Font.BOLD:Font.PLAIN,13));
            }
            display.removeAll();
            try{
                JComponent content;
                if(key.equals("HOME"))content=new DashboardPanel(user);
                else if(key.equals("DANGKY"))content=Workflows.registration(user);
                else if(key.equals("DISCHARGE"))content=Workflows.discharge(user);
                else if(key.equals("REPORT"))content=new ReportsPanel();
                else content=new DataPanel(Specs.byKey(key),user);
                display.add(content,BorderLayout.CENTER);
            }catch(Exception ex){
                JPanel problem=new JPanel(new BorderLayout());problem.setBackground(Theme.BG);
                JLabel msg=new JLabel("Không thể mở mục này: "+ex.getMessage());
                problem.add(msg,BorderLayout.NORTH);display.add(problem,BorderLayout.CENTER);
            }
            display.revalidate();display.repaint();
        }
    }
}
