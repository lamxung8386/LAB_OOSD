import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding","UTF-8");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                NghiepVu service=new NghiepVu();
                new GiaoDien(service).setVisible(true);
            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,ex.toString(),"Không khởi động được phần mềm",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
