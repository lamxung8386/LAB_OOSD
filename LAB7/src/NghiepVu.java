import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import static java.util.Comparator.comparing;

/** Quy tắc nghiệp vụ, kiểm tra dữ liệu và lưu tệp cục bộ (không cần thư viện ngoài). */
public class NghiepVu {
    public final Model.DuLieu db;
    public final Path duongDan;
    private final SqlLuuTru sql;
    private final boolean laSql;
    public String moTaLuuTru() {return laSql ? "SQL Server: QuanLyNhanSuDB (localhost)" : "File: "+duongDan;}
    public NghiepVu() {
        this.duongDan=Paths.get(System.getProperty("qlyns.data", System.getProperty("user.home")+"/.quanlynhansu/nhansu.dat"));
        Properties p=new Properties();
        Path cfg=Paths.get("ketnoi.properties");
        if(Files.exists(cfg)){
            try(InputStream in=Files.newInputStream(cfg)){p.load(in);}
            catch(IOException e){throw new IllegalStateException("Không đọc được ketnoi.properties",e);}
        }
        String mode=System.getProperty("qlyns.mode",System.getProperty("qlyns.data")!=null?"file":p.getProperty("che_do","file"));
        this.laSql="sqlserver".equalsIgnoreCase(mode);
        if (laSql) {
            sql=new SqlLuuTru(p);
            db=sql.doc();
            if (db.phongBan.isEmpty() && db.nguoiDung.isEmpty()) {duLieuMau();luu();}
        } else {
            sql=null;
            if (Files.exists(duongDan)) {
                try (ObjectInputStream in=new ObjectInputStream(Files.newInputStream(duongDan))) {
                    db=(Model.DuLieu)in.readObject();
                } catch (Exception ex) { throw new IllegalStateException("Không mở được dữ liệu: "+duongDan+". "+ex.getMessage(),ex); }
            } else { db=new Model.DuLieu(); duLieuMau(); luu(); }
        }
    }
    public void luu() {
        if (laSql) {sql.ghi(db);return;}
        try {
            Path dir=duongDan.toAbsolutePath().getParent();
            if(dir!=null) Files.createDirectories(dir);
            Path tmp=duongDan.resolveSibling(duongDan.getFileName().toString()+".tmp");
            try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(tmp))){out.writeObject(db);}
            try{Files.move(tmp,duongDan,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException e){Files.move(tmp,duongDan,StandardCopyOption.REPLACE_EXISTING);}
        } catch(IOException e){throw new IllegalStateException("Không lưu được dữ liệu: "+e.getMessage(),e);}
    }
    public static void yeuCau(boolean dk,String tb){if(!dk)throw new IllegalArgumentException(tb);}
    public static String chuoi(String s,String ten){yeuCau(s!=null&&!s.isBlank(),"Vui lòng nhập "+ten);return s.trim();}
    public static LocalDate ngay(String s){try{return LocalDate.parse(s);}catch(Exception e){throw new IllegalArgumentException("Ngày phải có dạng yyyy-MM-dd: "+s);}}
    public static void khoangNgay(String bd,String kt){yeuCau(!ngay(kt).isBefore(ngay(bd)),"Ngày kết thúc phải >= ngày bắt đầu");}
    public static double so(String s,String ten){try{double d=Double.parseDouble(s.replace(",", ".").trim());yeuCau(Double.isFinite(d)&&d>=0,ten+" phải >= 0");return d;}catch(NumberFormatException e){throw new IllegalArgumentException(ten+" phải là số, ví dụ: 8000000");}}
    private static void chuaCo(Map<String,?> m,String id){yeuCau(!m.containsKey(id),"Mã đã tồn tại: "+id);}
    private void coNhanVien(String id){yeuCau(db.nhanVien.containsKey(id),"Không tìm thấy nhân viên: "+id);}
    private static String id(String s){return chuoi(s,"mã");}
    private static boolean trung(Map<String,?> m,String id){return m.containsKey(id);}
    public Model.NguoiDung dangNhap(String tk,String mk){Model.NguoiDung nd=db.nguoiDung.get(tk);return nd!=null&&nd.matKhau.equals(mk)?nd:null;}
    public void doiMatKhau(Model.NguoiDung nd,String cu,String moi){yeuCau(nd.matKhau.equals(cu),"Mật khẩu cũ không đúng");yeuCau(moi.length()>=6,"Mật khẩu mới phải có ít nhất 6 ký tự");nd.matKhau=moi;luu();}

    public void themPhong(String ma,String ten,String truong){ma=id(ma);chuaCo(db.phongBan,ma);db.phongBan.put(ma,new Model.PhongBan(ma,chuoi(ten,"tên phòng"),truong.trim()));luu();}
    public void suaPhong(String ma,String ten,String truong){Model.PhongBan p=db.phongBan.get(ma);yeuCau(p!=null,"Không tìm thấy phòng ban");p.ten=chuoi(ten,"tên phòng");p.truongPhong=truong.trim();luu();}
    public void xoaPhong(String ma){yeuCau(db.phongBan.containsKey(ma),"Không tìm thấy phòng ban");
        yeuCau(db.nhanVien.values().stream().noneMatch(n->n.phongBan.equals(ma)),"Phòng ban đang có nhân viên");
        yeuCau(db.thuongPhat.values().stream().noneMatch(t->t.doiTuong.equals("PHONG_BAN")&&t.maDoiTuong.equals(ma)),"Phòng ban đã có quyết định thưởng/phạt");
        db.phongBan.remove(ma);luu();}
    public void themNhanVien(String ma,String ten,String phong,String ngayVao,String sdt){ma=id(ma);chuaCo(db.nhanVien,ma);
        yeuCau(db.phongBan.containsKey(phong),"Chọn phòng ban hợp lệ");ngay(ngayVao);
        db.nhanVien.put(ma,new Model.NhanVien(ma,chuoi(ten,"họ tên"),phong,ngayVao,sdt.trim(),"MOI"));luu();}
    public void suaNhanVien(String ma,String ten,String phong,String sdt){Model.NhanVien n=db.nhanVien.get(ma);yeuCau(n!=null,"Không tìm thấy nhân viên");
        yeuCau(db.phongBan.containsKey(phong),"Phòng ban không tồn tại");n.hoTen=chuoi(ten,"họ tên");n.phongBan=phong;n.sdt=sdt.trim();luu();}
    public void xoaNhanVien(String ma){yeuCau(db.nhanVien.containsKey(ma),"Không tìm thấy nhân viên");
        boolean daPhatSinh=db.tuyenDung.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.danhGia.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.hopDong.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.luong.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.chucVu.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.phanCong.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.chamCong.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.chiTietPhuTroi.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.bangLuong.values().stream().anyMatch(x->x.nhanVien.equals(ma))
            ||db.thuongPhat.values().stream().anyMatch(x->x.doiTuong.equals("CA_NHAN")&&x.maDoiTuong.equals(ma));
        yeuCau(!daPhatSinh,"Nhân viên đã có hồ sơ liên quan, không thể xóa để tránh mất lịch sử");
        yeuCau(db.nguoiDung.values().stream().noneMatch(x->ma.equals(x.nhanVien)),"Nhân viên gắn với tài khoản đăng nhập");
        db.nhanVien.remove(ma);luu();}

    public String tuyenDung(String nv,String ky,String bd,String kt,double luong,String dieuKhoan) {
        coNhanVien(nv);ngay(ky);khoangNgay(bd,kt);yeuCau(luong>=0,"Lương thử việc không hợp lệ");
        yeuCau(db.tuyenDung.values().stream().noneMatch(x->x.nhanVien.equals(nv)),"Nhân viên đã có quyết định tuyển dụng");
        Model.NhanVien n=db.nhanVien.get(nv);yeuCau(n.trangThai.equals("MOI"),"Chỉ tiếp nhận nhân viên mới");
        String ma=db.id("TD");db.tuyenDung.put(ma,new Model.QuyetDinhTuyenDung(ma,nv,ky,bd,kt,luong,chuoi(dieuKhoan,"điều khoản")));
        n.trangThai="THU_VIEC";luu();return ma;
    }
    public String danhGia(String nv,String ngayDG,boolean dat,String ghiChu){coNhanVien(nv);ngay(ngayDG);
        Model.QuyetDinhTuyenDung td=db.tuyenDung.values().stream().filter(x->x.nhanVien.equals(nv)).findFirst().orElse(null);
        yeuCau(td!=null,"Nhân viên chưa có quyết định thử việc");
        yeuCau(!ngay(ngayDG).isBefore(ngay(td.ketThuc)),"Chưa hết thời gian thử việc");
        yeuCau(db.danhGia.values().stream().noneMatch(x->x.nhanVien.equals(nv)),"Nhân viên đã được đánh giá");
        yeuCau(db.nhanVien.get(nv).trangThai.equals("THU_VIEC"),"Nhân viên không ở trạng thái thử việc");
        String ma=db.id("DG");db.danhGia.put(ma,new Model.DanhGiaThuViec(ma,nv,ngayDG,dat?"DAT":"KHONG_DAT",ghiChu));
        db.nhanVien.get(nv).trangThai=dat?"DAT_THU_VIEC":"KET_THUC";luu();return ma;
    }
    public String hopDong(String nv,String loai,String ngayKy,String bd,String kt,String bhxh,String bhyt,double mucDong){coNhanVien(nv);
        yeuCau(loai.equals("DAI_HAN")||loai.equals("NGAN_HAN"),"Loại hợp đồng không hợp lệ");ngay(ngayKy);ngay(bd);
        if(loai.equals("NGAN_HAN")) khoangNgay(bd,kt); else if(!kt.isBlank())khoangNgay(bd,kt);
        yeuCau(db.nhanVien.get(nv).trangThai.equals("DAT_THU_VIEC"),"Phải đánh giá thử việc ĐẠT trước khi ký hợp đồng");
        yeuCau(db.hopDong.values().stream().noneMatch(x->x.nhanVien.equals(nv)),"Đã có hợp đồng của nhân viên");
        if(loai.equals("DAI_HAN")){chuoi(bhxh,"số BHXH");chuoi(bhyt,"số BHYT");yeuCau(mucDong>=0,"Mức đóng bảo hiểm không hợp lệ");}
        String ma=db.id("HD");db.hopDong.put(ma,new Model.HopDong(ma,nv,loai,ngayKy,bd,kt));
        if(loai.equals("DAI_HAN")){String maBh=db.id("BH");db.baoHiem.put(maBh,new Model.HoSoBaoHiem(maBh,ma,bhxh,bhyt,mucDong));}
        db.nhanVien.get(nv).trangThai="CHINH_THUC";luu();return ma;
    }
    public String quyetDinhLuong(String nv,String hl,double cb,double pc,double dh){coNhanVien(nv);ngay(hl);yeuCau(cb>0&&pc>=0&&dh>=0,"Lương và phụ cấp không hợp lệ");
        yeuCau(db.nhanVien.get(nv).trangThai.equals("CHINH_THUC"),"Cần ký hợp đồng trước khi ra quyết định lương");
        String ma=db.id("L");db.luong.put(ma,new Model.QuyetDinhLuong(ma,nv,hl,cb,pc,dh));luu();return ma;
    }
    public String quyetDinhChucVu(String nv,String hl,String cv,double pc){coNhanVien(nv);ngay(hl);yeuCau(pc>=0,"Phụ cấp không hợp lệ");
        yeuCau(db.nhanVien.get(nv).trangThai.equals("CHINH_THUC"),"Nhân viên chưa chính thức");
        String ma=db.id("CV");db.chucVu.put(ma,new Model.QuyetDinhChucVu(ma,nv,chuoi(cv,"chức vụ"),hl,pc));luu();return ma;
    }
    public String phanCong(String nv,String ngayLam,String ca,double gio){coNhanVien(nv);ngay(ngayLam);yeuCau(gio>0&&gio<=24,"Số giờ phân công phải từ 0 đến 24");
        String ma=db.id("PC");db.phanCong.put(ma,new Model.KeHoachPhanCong(ma,nv,ngayLam,chuoi(ca,"ca"),gio));luu();return ma;
    }
    public String chamCong(String nv,int t,int n,double cong,double gio){coNhanVien(nv);yeuCau(t>=1&&t<=12&&n>=2000&&n<=2100,"Tháng/năm không hợp lệ");
        yeuCau(cong>=0&&cong<=31&&gio>=0&&gio<=31*24,"Ngày công/giờ công không hợp lệ");
        Model.ChamCong cc=db.chamCong.values().stream().filter(x->x.nhanVien.equals(nv)&&x.thang==t&&x.nam==n).findFirst().orElse(null);
        if(cc!=null){cc.ngayCong=cong;cc.gioLam=gio;luu();return cc.ma;}
        String ma=db.id("CC");db.chamCong.put(ma,new Model.ChamCong(ma,nv,t,n,cong,gio));luu();return ma;
    }
    public String phieuPhuTroi(String ngay,String hinhThuc){ngay(ngay);yeuCau(Set.of("TANG_CA","NGAY_LE","NGAY_NGHI").contains(hinhThuc),"Hình thức phụ trội không hợp lệ");
        String ma=db.id("PT");db.phuTroi.put(ma,new Model.PhieuPhuTroi(ma,ngay,hinhThuc));luu();return ma;
    }
    public String chiTietPhuTroi(String phieu,String nv,double gio){yeuCau(db.phuTroi.containsKey(phieu),"Phiếu phụ trội không tồn tại");coNhanVien(nv);
        yeuCau(gio>0&&gio<=24,"Giờ phụ trội phải > 0 và <= 24");
        yeuCau(db.chiTietPhuTroi.values().stream().noneMatch(x->x.phieu.equals(phieu)&&x.nhanVien.equals(nv)),"Nhân viên đã có chi tiết trong phiếu này");
        String ma=db.id("CT");db.chiTietPhuTroi.put(ma,new Model.ChiTietPhuTroi(ma,phieu,nv,gio));luu();return ma;
    }
    public String lapThuongPhat(String loai,String doiTuong,String maDt,String ngayLap,String lyDo,double tien,String hinhThuc){
        yeuCau(Set.of("THUONG","KY_LUAT").contains(loai),"Loại quyết định không hợp lệ");
        yeuCau(Set.of("CA_NHAN","PHONG_BAN").contains(doiTuong),"Đối tượng không hợp lệ");
        yeuCau(doiTuong.equals("CA_NHAN")?db.nhanVien.containsKey(maDt):db.phongBan.containsKey(maDt),"Không tìm thấy đối tượng");
        ngay(ngayLap);yeuCau(tien>0,"Số tiền phải lớn hơn 0");
        yeuCau(Set.of("TIEN_MAT","QUA_LUONG").contains(hinhThuc),"Hình thức thanh toán không hợp lệ");
        yeuCau(!doiTuong.equals("PHONG_BAN")||hinhThuc.equals("TIEN_MAT"),"Thưởng/phạt tập thể chỉ dùng phiếu tiền mặt (chưa có tỷ lệ chia cá nhân)");
        String ma=db.id("QD");db.thuongPhat.put(ma,new Model.QuyDinhThuongPhat(ma,loai,doiTuong,maDt,chuoi(lyDo,"lý do"),ngayLap,tien,hinhThuc));luu();return ma;
    }
    public void duyet(String ma,boolean chapThuan,String nguoiKy){Model.QuyDinhThuongPhat q=db.thuongPhat.get(ma);yeuCau(q!=null,"Không tìm thấy quyết định");
        yeuCau(q.trangThai.equals("CHO_DUYET"),"Quyết định đã được xử lý");
        q.trangThai=chapThuan?"DA_DUYET":"TU_CHOI";q.nguoiKy=chapThuan?chuoi(nguoiKy,"người ký"):"";luu();
    }
    public String phieuThuChi(String maQD,String ngayLap,String ghiChu){Model.QuyDinhThuongPhat q=db.thuongPhat.get(maQD);
        yeuCau(q!=null&&q.trangThai.equals("DA_DUYET"),"Quyết định chưa được phê duyệt");
        yeuCau(q.hinhThuc.equals("TIEN_MAT"),"Quyết định này chi trả/khấu trừ qua bảng lương, không lập phiếu riêng");
        yeuCau(db.thuChi.values().stream().noneMatch(x->x.quyetDinh.equals(maQD)),"Quyết định đã có phiếu thu/chi");ngay(ngayLap);
        String ma=db.id("TC");db.thuChi.put(ma,new Model.PhieuThuChi(ma,maQD,q.loai.equals("THUONG")?"CHI_THUONG":"THU_PHAT",ngayLap,q.soTien,ghiChu));luu();return ma;
    }
    private Model.QuyetDinhLuong luongHieuLuc(String nv,LocalDate ngayCuoi){return db.luong.values().stream()
        .filter(x->x.nhanVien.equals(nv)&&!ngay(x.hieuLuc).isAfter(ngayCuoi))
        .max(comparing(x->ngay(x.hieuLuc))).orElse(null);}
    private Model.QuyetDinhChucVu chucVuHieuLuc(String nv,LocalDate ngayCuoi){return db.chucVu.values().stream()
        .filter(x->x.nhanVien.equals(nv)&&!ngay(x.hieuLuc).isAfter(ngayCuoi))
        .max(comparing(x->ngay(x.hieuLuc))).orElse(null);}
    public Model.BangLuong tinhLuong(String nv,int thang,int nam){coNhanVien(nv);yeuCau(thang>=1&&thang<=12&&nam>=2000&&nam<=2100,"Tháng năm không hợp lệ");
        Model.ChamCong cc=db.chamCong.values().stream().filter(x->x.nhanVien.equals(nv)&&x.thang==thang&&x.nam==nam).findFirst().orElse(null);
        yeuCau(cc!=null,"Chưa có chấm công tháng "+thang+"/"+nam);
        LocalDate cuoi=YearMonth.of(nam,thang).atEndOfMonth();
        Model.QuyetDinhLuong ql=luongHieuLuc(nv,cuoi);
        Model.QuyetDinhTuyenDung td=db.tuyenDung.values().stream().filter(x->x.nhanVien.equals(nv)).findFirst().orElse(null);
        Model.HopDong hd=db.hopDong.values().stream().filter(x->x.nhanVien.equals(nv)&&!ngay(x.batDau).isAfter(cuoi))
            .max(comparing(x->ngay(x.batDau))).orElse(null);
        double cb;
        if(hd!=null){yeuCau(ql!=null,"Nhân viên chính thức chưa có quyết định lương có hiệu lực");cb=ql.luongCoBan;}
        else{yeuCau(td!=null,"Chưa có quyết định tuyển dụng");cb=td.luongThuViec;}
        double luongCong=cb/26.0*cc.ngayCong;
        double phuCap=hd!=null ? ql.phuCap+ql.docHai : 0;
        Model.QuyetDinhChucVu cv=chucVuHieuLuc(nv,cuoi);if(hd!=null&&cv!=null) phuCap+=cv.phuCap;
        double soGioPhuTroi=db.chiTietPhuTroi.values().stream().filter(x->x.nhanVien.equals(nv))
            .filter(x->{Model.PhieuPhuTroi p=db.phuTroi.get(x.phieu);return p!=null&&ngay(p.ngay).getMonthValue()==thang&&ngay(p.ngay).getYear()==nam;})
            .mapToDouble(x->x.gio).sum();
        double tienPhuTroi=soGioPhuTroi * (cb / 26.0 / 8.0) * 1.5;
        double thuong=0,phat=0;
        for(Model.QuyDinhThuongPhat q:db.thuongPhat.values()) {
            LocalDate d=ngay(q.ngay);
            if(q.trangThai.equals("DA_DUYET")&&q.hinhThuc.equals("QUA_LUONG")&&q.doiTuong.equals("CA_NHAN")&&q.maDoiTuong.equals(nv)
                &&d.getMonthValue()==thang&&d.getYear()==nam){if(q.loai.equals("THUONG"))thuong+=q.soTien;else phat+=q.soTien;}
        }
        double bh=0;
        if(hd!=null&&hd.loai.equals("DAI_HAN")){
            final String maHd=hd.ma;
            bh=db.baoHiem.values().stream().filter(x->x.hopDong.equals(maHd)).mapToDouble(x->x.mucDongThang).sum();
        }
        return new Model.BangLuong("BL-"+nam+"-"+String.format("%02d",thang)+"-"+nv,nv,thang,nam,luongCong,phuCap,tienPhuTroi,thuong,phat,bh);
    }
    public Model.BangLuong luuBangLuong(String nv,int t,int n){Model.BangLuong b=tinhLuong(nv,t,n);db.bangLuong.put(b.ma,b);luu();return b;}

    private void duLieuMau(){
        db.phongBan.put("PB01",new Model.PhongBan("PB01","Kế toán - Tài chính","Nguyễn Minh Anh"));
        db.phongBan.put("PB02",new Model.PhongBan("PB02","Kỹ thuật","Trần Quốc Bình"));
        db.phongBan.put("PB03",new Model.PhongBan("PB03","Sản xuất","Lê Thanh Hà"));
        db.phongBan.put("PB04",new Model.PhongBan("PB04","Nhân sự","Phạm Ngọc Dung"));
        db.nhanVien.put("NV001",new Model.NhanVien("NV001","Nguyễn Minh Anh","PB01","2024-01-15","0900000001","CHINH_THUC"));
        db.nhanVien.put("NV002",new Model.NhanVien("NV002","Trần Quốc Bình","PB02","2024-05-10","0900000002","CHINH_THUC"));
        db.nhanVien.put("NV003",new Model.NhanVien("NV003","Lê Thanh Hà","PB03","2025-03-08","0900000003","CHINH_THUC"));
        db.nhanVien.put("NV004",new Model.NhanVien("NV004","Phạm Ngọc Dung","PB04","2024-02-01","0900000004","CHINH_THUC"));
        db.nhanVien.put("NV005",new Model.NhanVien("NV005","Đỗ Gia Huy","PB03","2026-07-01","0900000005","THU_VIEC"));
        db.nhanVien.put("NV006",new Model.NhanVien("NV006","Vũ Thu Thảo","PB02","2026-07-01","0900000006","DAT_THU_VIEC"));
        db.tuyenDung.put("TD0001",new Model.QuyetDinhTuyenDung("TD0001","NV005","2026-06-28","2026-07-01","2026-08-31",6500000,"Thử việc theo quy định"));
        db.tuyenDung.put("TD0002",new Model.QuyetDinhTuyenDung("TD0002","NV006","2026-06-28","2026-07-01","2026-08-31",7000000,"Thử việc theo quy định"));
        db.danhGia.put("DG0001",new Model.DanhGiaThuViec("DG0001","NV006","2026-09-01","DAT","Đáp ứng công việc"));
        for(int i=1;i<=4;i++) {
            String nv=String.format("NV%03d",i),hd=String.format("HD%04d",i),qd=String.format("L%04d",i);
            String loai=i==3?"NGAN_HAN":"DAI_HAN";
            db.hopDong.put(hd,new Model.HopDong(hd,nv,loai,"2025-01-01","2025-01-01",loai.equals("NGAN_HAN")?"2027-12-31":""));
            db.luong.put(qd,new Model.QuyetDinhLuong(qd,nv,"2025-01-01",9500000+i*750000,450000,i==3?350000:0));
            if(loai.equals("DAI_HAN"))db.baoHiem.put("BH"+i,new Model.HoSoBaoHiem("BH"+i,hd,"BHXH-"+i,"BHYT-"+i,800000+i*30000));
        }
        db.chucVu.put("CV0001",new Model.QuyetDinhChucVu("CV0001","NV002","Trưởng phòng","2025-01-01",1500000));
        int t=LocalDate.now().getMonthValue(), n=LocalDate.now().getYear();
        for(int i=1;i<=4;i++){String nv=String.format("NV%03d",i);db.chamCong.put("CC"+i,new Model.ChamCong("CC"+i,nv,t,n,24,192));}
        db.phuTroi.put("PT0001",new Model.PhieuPhuTroi("PT0001",LocalDate.now().withDayOfMonth(5).toString(),"TANG_CA"));
        db.chiTietPhuTroi.put("CT0001",new Model.ChiTietPhuTroi("CT0001","PT0001","NV002",8));
        Model.QuyDinhThuongPhat q=new Model.QuyDinhThuongPhat("QD0001","THUONG","CA_NHAN","NV001","Hoàn thành vượt chỉ tiêu",LocalDate.now().withDayOfMonth(6).toString(),500000,"QUA_LUONG");
        q.trangThai="DA_DUYET";q.nguoiKy="Giám đốc";db.thuongPhat.put(q.ma,q);
        db.thuongPhat.put("QD0002",new Model.QuyDinhThuongPhat("QD0002","THUONG","PHONG_BAN","PB03","Tập thể hoàn thành tiến độ",LocalDate.now().withDayOfMonth(7).toString(),1500000,"TIEN_MAT"));
        for(String role:new String[]{"nhansu","truongphong","chamcong","ketoan","giamdoc","nhanvien"}){
            String v=switch(role){case "nhansu"->"NHAN_SU";case "truongphong"->"TRUONG_PHONG";case "chamcong"->"CHAM_CONG";case "ketoan"->"KE_TOAN";case "giamdoc"->"GIAM_DOC";default->"NHAN_VIEN";};
            db.nguoiDung.put(role,new Model.NguoiDung(role,"123456",v,role.equals("nhanvien")?"NV001":""));
        }
    }
}
