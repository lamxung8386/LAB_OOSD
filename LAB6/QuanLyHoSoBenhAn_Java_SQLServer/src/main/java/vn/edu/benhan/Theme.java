package vn.edu.benhan;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public final class Theme {
    public static final Color NAVY = new Color(22, 45, 75), BLUE = new Color(24, 103, 193),
            BG = new Color(245, 248, 252), WHITE = Color.WHITE, TEXT = new Color(30, 48, 66),
            MUTED = new Color(109, 124, 139), STROKE = new Color(222, 231, 240);
    private Theme() {}
    public static void configure() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Table.rowHeight", 31);
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
    }
    public static JLabel title(String value) {
        JLabel l = new JLabel(value);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22)); l.setForeground(NAVY); return l;
    }
    public static JLabel caption(String value) {
        JLabel l = new JLabel(value);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12)); l.setForeground(MUTED); return l;
    }
    public static JButton button(String label) {
        JButton b = new JButton(label);
        // BasicButtonUI honors explicit foreground/background colors on Windows L&F.
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(BLUE); b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(9, 15, 9, 15)); return b;
    }
    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout); p.setBackground(WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(STROKE, 1, true), new EmptyBorder(16, 16, 16, 16)));
        return p;
    }
    public static void error(Component parent, Exception e) {
        String msg = e.getMessage();
        JOptionPane.showMessageDialog(parent, msg == null ? e.toString() : msg,
                "Có lỗi", JOptionPane.ERROR_MESSAGE);
    }
    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
