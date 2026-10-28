package ngk;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import static ngk.Models.*;

/** Kiểm tra quy tắc nghiệp vụ mà không cần mở cửa sổ. */
public final class BusinessTests {
    static int passed=0;
    private static void check(boolean condition, String message) {
        if(!condition)throw new AssertionError(message);
        System.out.println("[ĐẠT] "+message);passed++;
    }
    private static void rejects(Runnable action,String expected) {
        try { action.run();throw new AssertionError("Lẽ ra phải bị từ chối: "+expected); }
        catch(IllegalArgumentException e){System.out.println("[ĐẠT] Từ chối "+expected); passed++;}
    }
    public static void main(String[] args) throws Exception {
        BusinessService s=new BusinessService(BusinessService.sample());
        check(s.db.products.size()==4,"Có dữ liệu sản phẩm mẫu");
        PurchaseOrder order=s.order("DDH001");
        rejects(()->s.createOrder("NCC001",LocalDate.now().plusDays(1),Map.of("NGK003",1)),"đặt hàng sai nhà cung cấp");
        s.receiveOrder("DDH001",LocalDate.now(),Map.of("NGK001",10,"NGK002",10));
        check(s.product("NGK001").stock==30,"Nhận hàng cập nhật tồn kho");
        s.receiveOrder("DDH001",LocalDate.now(),Map.of("NGK001",10,"NGK002",10));
        s.receiveOrder("DDH001",LocalDate.now(),Map.of("NGK001",10));
        check(order.deliveries.size()==3,"Đơn đặt hàng có 3 đợt giao");
        rejects(()->s.receiveOrder("DDH001",LocalDate.now(),Map.of("NGK001",10)),"giao vượt 3 đợt");
        rejects(()->s.sell("KH001",Map.of("NGK004",100),false,null,null,""),"khách vãng lai mua vượt kho");
        rejects(()->s.sell("KH001",Map.of("NGK001",1),true,null,LocalDate.now().plusDays(5),"nợ"),"ghi nợ cho khách vãng lai");
        Invoice regular=s.sell("KH002",Map.of("NGK004",12,"NGK003",2),true,LocalDate.now().plusDays(2),LocalDate.now().plusDays(10),"Hẹn trả sau");
        check(regular.amount()==92000,"Hóa đơn tính đúng thành tiền");
        check(s.product("NGK004").stock==0,"Giao ngay phần hàng có trong kho");
        check(s.db.appointments.size()==1 && s.db.appointments.get(0).remaining()==7,"Tạo phiếu hẹn số hàng còn thiếu");
        check(s.db.debts.size()==1 && s.pendingDebts()==92000,"Tạo phiếu ghi nợ cho khách quen");
        Appointment first=s.db.appointments.get(0);
        Appointment split=s.splitAppointment(first.id,3,first.promisedOn.plusDays(1));
        check(first.remaining()==4 && split.remaining()==3,"Tách hẹn thành hai đợt không mất số lượng");
        rejects(()->s.deliverAppointment(first.id,1),"giao hàng khi kho đã hết");
        PurchaseOrder water=s.createOrder("NCC002",LocalDate.now().plusDays(1),Map.of("NGK004",20));
        s.receiveOrder(water.id,LocalDate.now(),Map.of("NGK004",20));
        s.deliverAppointment(first.id,4);
        s.deliverAppointment(split.id,3);
        check(regular.delivered() && s.product("NGK004").stock==13,"Giao hết theo hai phiếu hẹn, cập nhật tồn kho");
        s.payDebt(s.db.debts.get(0).id,40000);
        check(s.pendingDebts()==52000,"Hỗ trợ thu nợ một phần");
        rejects(()->s.payDebt(s.db.debts.get(0).id,60000),"thu nợ vượt dư nợ");
        s.payDebt(s.db.debts.get(0).id,52000);
        check(s.pendingDebts()==0,"Thu hết khoản nợ");
        Invoice walkin=s.sell("KH001",Map.of("NGK003",1),false,null,null,"");
        check(walkin.delivered() && walkin.paidImmediately,"Khách vãng lai giao và thanh toán ngay");
        Path file=Files.createTempFile("ngk-bai2-test-",".dat");
        DataStore dataStore=new DataStore(file);dataStore.save(s.db);
        check(dataStore.load().invoices.size()==2,"Lưu và khôi phục dữ liệu thành công");
        Files.deleteIfExists(file);
        System.out.println("HOÀN THÀNH: " + passed + " bài kiểm tra.");
    }
}
