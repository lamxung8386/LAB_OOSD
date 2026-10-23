import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

/** Kiểm tra mở màn hình và các menu, chỉ dùng khi có môi trường đồ họa. */
public class KiemThuGiaoDien {
    private static JButton findButton(Component parent,String text){
        if(parent instanceof JButton b && text.equals(b.getText()))return b;
        if(parent instanceof Container c)for(Component child:c.getComponents()){JButton f=findButton(child,text);if(f!=null)return f;}
        return null;
    }
    private static JComboBox<?> findCombo(Component parent){
        if(parent instanceof JComboBox<?> cb)return cb;
        if(parent instanceof Container c)for(Component ch:c.getComponents()){JComboBox<?> found=findCombo(ch);if(found!=null)return found;}
        return null;
    }
    public static void main(String[] args)throws Exception{
        System.setProperty("qlyns.data",System.getProperty("java.io.tmpdir")+"/nhansu-gui-smoke.dat");
        final GiaoDien[] box=new GiaoDien[1];
        SwingUtilities.invokeAndWait(()->{box[0]=new GiaoDien(new NghiepVu());box[0].setVisible(true);});
        SwingUtilities.invokeAndWait(()->{
            JButton login=findButton(box[0],"Đăng nhập");if(login==null)throw new AssertionError("Không tìm được nút đăng nhập");login.doClick();
        });
        for(String menu:new String[]{"Nhân viên","Tuyển dụng","Hợp đồng","Quyết định lương","Thưởng / kỷ luật","Tổng quan"}){
            SwingUtilities.invokeAndWait(()->{
                JButton b=findButton(box[0],menu);if(b==null)throw new AssertionError("Thiếu menu "+menu);b.doClick();
            });
        }
        // Thử chuyển qua 5 vai trò còn lại và mở toàn bộ màn hình được phân quyền.
        String[][] accounts={
            {"truongphong","Đánh giá thử việc","Phân công ca làm"},
            {"chamcong","Bảng chấm công","Phiếu phụ trội"},
            {"ketoan","Bảng lương","Phiếu thu / chi"},
            {"giamdoc","Duyệt thưởng / phạt"},
            {"nhanvien","Hồ sơ của tôi","Lương của tôi"}
        };
        for(String[] role:accounts){
            SwingUtilities.invokeAndWait(()->{
                findButton(box[0],"Đăng xuất").doClick();
                JComboBox<?> account=findCombo(box[0]);
                if(account==null)throw new AssertionError("Không tìm thấy tài khoản");
                account.setSelectedItem(role[0]);
                findButton(box[0],"Đăng nhập").doClick();
                for(int i=1;i<role.length;i++){
                    JButton b=findButton(box[0],role[i]);
                    if(b==null)throw new AssertionError("Thiếu màn hình "+role[i]);
                    b.doClick();
                }
            });
        }
        SwingUtilities.invokeAndWait(()->{
            findButton(box[0],"Đăng xuất").doClick();
            findButton(box[0],"Đăng nhập").doClick();
        });
        Thread.sleep(500);
        Rectangle rect=box[0].getBounds();
        BufferedImage img=new Robot().createScreenCapture(rect);
        ImageIO.write(img,"png",new File("anh_giao_dien.png"));
        SwingUtilities.invokeAndWait(()->box[0].dispose());
        System.out.println("[PASS] Mở đăng nhập, kiểm tra đầy đủ màn hình 6 vai trò và chụp ảnh.");
        System.exit(0);
    }
}
