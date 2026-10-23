import java.nio.file.*;
import java.util.*;

/** Kiểm thử độc lập, chạy: java -cp out KiemThu */
public class KiemThu {
    private static int passed=0;
    private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);passed++;System.out.println("[PASS] "+message);}
    private static void failExpected(Runnable r,String message){try{r.run();throw new AssertionError("Dự kiến bị từ chối: "+message);}catch(IllegalArgumentException e){passed++;System.out.println("[PASS] Chặn sai: "+message);}}
    private static void nearly(double a,double b,String message){check(Math.abs(a-b)<0.001,message+" ("+a+")");}
    public static void main(String[] args)throws Exception{
        Path temp=Files.createTempDirectory("nhansu-kiemthu-");
        System.setProperty("qlyns.data",temp.resolve("db.dat").toString());
        NghiepVu s=new NghiepVu();
        check(s.dangNhap("nhansu","123456")!=null,"Đăng nhập nhân sự");
        check(s.dangNhap("nhansu","sai")==null,"Từ chối mật khẩu sai");
        check(s.db.nhanVien.size()==6,"Tạo dữ liệu mẫu 6 nhân viên");
        s.themPhong("PB05","Kho vận","Nguyễn Văn A");
        s.themNhanVien("NV100","Nguyễn Văn Test","PB05","2026-01-01","0909999999");
        check(s.db.nhanVien.get("NV100").trangThai.equals("MOI"),"Thêm hồ sơ nhân viên");
        failExpected(()->s.hopDong("NV100","NGAN_HAN","2026-03-01","2026-03-01","2027-03-01","","",0),"Không ký hợp đồng khi chưa đạt thử việc");
        failExpected(()->s.tuyenDung("NV100","2026-01-01","2026-04-01","2026-02-01",6000000,"Theo QD"),"Không nhận thời gian thử việc ngược");
        String td=s.tuyenDung("NV100","2025-12-30","2026-01-01","2026-02-28",6000000,"Theo quyết định");
        check(s.db.tuyenDung.containsKey(td),"Lập quyết định tuyển dụng");
        failExpected(()->s.danhGia("NV100","2026-02-20",true,"Đạt"),"Không đánh giá trước khi hết thử việc");
        s.danhGia("NV100","2026-03-01",true,"Đạt");
        check(s.db.nhanVien.get("NV100").trangThai.equals("DAT_THU_VIEC"),"Trưởng phòng đánh giá đạt");
        failExpected(()->s.hopDong("NV100","DAI_HAN","2026-03-02","2026-03-02","","","",0),"HĐ dài hạn bắt buộc BHXH/BHYT");
        s.hopDong("NV100","NGAN_HAN","2026-03-02","2026-03-02","2027-03-02","","",0);
        check(s.db.nhanVien.get("NV100").trangThai.equals("CHINH_THUC"),"Ký hợp đồng ngắn hạn");
        s.quyetDinhLuong("NV100","2026-03-02",10000000,300000,0);
        s.quyetDinhChucVu("NV100","2026-03-02","Chuyên viên",700000);
        s.phanCong("NV100","2026-04-10","Ca sáng",8);
        check(s.db.phanCong.size()==1,"Lập kế hoạch phân công");
        s.chamCong("NV100",4,2026,26,208);
        s.chamCong("NV100",4,2026,25,200);
        check(s.db.chamCong.values().stream().filter(x->x.nhanVien.equals("NV100")).count()==1,"Chấm công trùng tháng được cập nhật");
        s.chamCong("NV100",4,2026,26,208);
        String pt=s.phieuPhuTroi("2026-04-15","TANG_CA");
        s.chiTietPhuTroi(pt,"NV100",4);
        failExpected(()->s.chiTietPhuTroi(pt,"NV100",3),"Không trùng chi tiết phụ trội cùng phiếu");
        String reward=s.lapThuongPhat("THUONG","CA_NHAN","NV100","2026-04-20","Đạt năng suất",500000,"QUA_LUONG");
        String penalty=s.lapThuongPhat("KY_LUAT","CA_NHAN","NV100","2026-04-21","Đi muộn",100000,"QUA_LUONG");
        nearly(s.tinhLuong("NV100",4,2026).thuong,0,"Chưa duyệt thì thưởng không vào lương");
        failExpected(()->s.phieuThuChi(reward,"2026-04-25",""),"Không lập phiếu khi chưa duyệt");
        s.duyet(reward,true,"Giám đốc");s.duyet(penalty,true,"Giám đốc");
        failExpected(()->s.duyet(reward,true,"Giám đốc"),"Không duyệt hai lần");
        failExpected(()->s.phieuThuChi(reward,"2026-04-25",""),"Không lập phiếu riêng cho tiền qua lương");
        Model.BangLuong b=s.luuBangLuong("NV100",4,2026);
        nearly(b.luongCong,10000000,"Lương đủ 26 công");
        nearly(b.phuCap,1000000,"Phụ cấp lương và chức vụ");
        nearly(b.tienPhuTroi,4.0*10000000/26/8*1.5,"Phụ trội tính hệ số 1,5");
        nearly(b.thuong,500000,"Thưởng đã duyệt vào lương");
        nearly(b.phat,100000,"Phạt đã duyệt vào lương");
        nearly(b.thucLinh,10000000+1000000+4.0*10000000/26/8*1.5+500000-100000,"Tổng thực lĩnh ngắn hạn");
        String cash=s.lapThuongPhat("THUONG","PHONG_BAN","PB05","2026-04-22","Tập thể xuất sắc",1800000,"TIEN_MAT");
        failExpected(()->s.lapThuongPhat("THUONG","PHONG_BAN","PB05","2026-04-22","Tập thể",1000000,"QUA_LUONG"),"Không tự chia tiền thưởng tập thể qua lương");
        s.duyet(cash,true,"Giám đốc");
        String receipt=s.phieuThuChi(cash,"2026-04-23","Đã chi");
        check(s.db.thuChi.containsKey(receipt),"Lập phiếu chi thưởng tập thể");
        failExpected(()->s.phieuThuChi(cash,"2026-04-23","Chi lại"),"Không chi hai lần");
        s.themNhanVien("NV101","Nhân viên BH","PB05","2026-01-01","0909999998");
        s.tuyenDung("NV101","2026-01-01","2026-01-02","2026-02-15",6000000,"Thử việc");
        s.danhGia("NV101","2026-02-16",true,"Đạt");
        String hd=s.hopDong("NV101","DAI_HAN","2026-03-01","2026-03-01","","123-BHXH","123-BHYT",900000);
        check(s.db.baoHiem.values().stream().anyMatch(x->x.hopDong.equals(hd)),"Tạo BH cho hợp đồng dài hạn");
        s.quyetDinhLuong("NV101","2026-03-01",11000000,0,0);s.chamCong("NV101",4,2026,26,208);
        nearly(s.luuBangLuong("NV101",4,2026).thucLinh,10100000,"Trích BH khỏi lương dài hạn");
        s.themNhanVien("NV102","Thử việc không đạt","PB05","2026-01-01","0909999997");
        s.tuyenDung("NV102","2026-01-01","2026-01-02","2026-02-15",4000000,"Thử việc");
        s.danhGia("NV102","2026-02-16",false,"Không đạt");
        check(s.db.nhanVien.get("NV102").trangThai.equals("KET_THUC"),"Kết thúc thử việc nếu không đạt");
        failExpected(()->s.hopDong("NV102","NGAN_HAN","2026-02-20","2026-02-20","2026-12-31","","",0),"Không ký hợp đồng cho nhân viên không đạt");
        failExpected(()->s.xoaNhanVien("NV100"),"Không xóa nhân viên có hồ sơ liên kết");
        NghiepVu reloaded=new NghiepVu();
        check(reloaded.db.bangLuong.containsKey(b.ma),"Đọc lại dữ liệu đã lưu khi khởi động lần 2");
        check(reloaded.db.thuChi.containsKey(receipt),"Lưu phiếu thu/chi bền vững");
        System.out.println("HOÀN THÀNH: "+passed+" bài kiểm thử. Dữ liệu thử nằm tại: "+temp);
    }
}
