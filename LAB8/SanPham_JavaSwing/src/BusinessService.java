package ngk;

import java.util.*;
import java.time.LocalDate;
import static ngk.Models.*;

/** Các quy tắc nghiệp vụ, tách riêng khỏi giao diện để kiểm thử. */
public final class BusinessService {
    public final Database db;
    public BusinessService(Database db) { this.db = Objects.requireNonNull(db); }

    public static Database sample() {
        Database db = new Database();
        db.brands.add(new Brand("L001", "Coca-Cola"));
        db.brands.add(new Brand("L002", "Pepsi"));
        db.brands.add(new Brand("L003", "Nước suối"));
        db.suppliers.add(new Supplier("NCC001", "Phân phối An Phú", "Quận 1, TP.HCM", "0901111222"));
        db.suppliers.add(new Supplier("NCC002", "Phân phối Bình Minh", "Quận 3, TP.HCM", "0903333444"));
        db.products.add(new Product("NGK001", "Coca-Cola lon 330ml", "L001", "NCC001", "Lon", 7000, 11000, 20));
        db.products.add(new Product("NGK002", "Sprite lon 330ml", "L001", "NCC001", "Lon", 7000, 11000, 8));
        db.products.add(new Product("NGK003", "Pepsi lon 330ml", "L002", "NCC002", "Lon", 6500, 10000, 15));
        db.products.add(new Product("NGK004", "Nước suối 500ml", "L003", "NCC002", "Chai", 3500, 6000, 5));
        db.customers.add(new Customer("KH001", "Khách mua tại cửa hàng", "", "", false));
        db.customers.add(new Customer("KH002", "Nguyễn Văn An", "0906666777", "TP.HCM", true));
        db.customers.add(new Customer("KH003", "Trần Thị Bình", "0908888999", "TP.HCM", true));
        db.employees.add(new Employee("NV001", "Nhân viên cửa hàng"));
        PurchaseOrder order = new PurchaseOrder("DDH001", "NCC001", "NV001", LocalDate.now(), LocalDate.now().plusDays(3));
        order.lines.add(new Line("NGK001", 40, 7000));
        order.lines.add(new Line("NGK002", 20, 7000));
        db.orders.add(order);
        return db;
    }
    public static String required(String s, String label) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập " + label + ".");
        return s.trim();
    }
    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
    public static int positive(int n, String what) {
        require(n > 0, what + " phải lớn hơn 0."); return n;
    }
    public static String nextId(String prefix, Collection<String> existing) {
        int max = 0;
        for (String s : existing) if (s.startsWith(prefix)) {
            try { max = Math.max(max, Integer.parseInt(s.substring(prefix.length()))); }
            catch (NumberFormatException ignored) {}
        }
        return prefix + String.format("%03d", max + 1);
    }
    public String brandId() { return nextId("L", db.brands.stream().map(x->x.id).toList()); }
    public String supplierId() { return nextId("NCC", db.suppliers.stream().map(x->x.id).toList()); }
    public String productId() { return nextId("NGK", db.products.stream().map(x->x.id).toList()); }
    public String customerId() { return nextId("KH", db.customers.stream().map(x->x.id).toList()); }
    public Brand brand(String id) { return db.brands.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có nhãn hiệu " + id)); }
    public Supplier supplier(String id) { return db.suppliers.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có nhà cung cấp " + id)); }
    public Product product(String id) { return db.products.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có mặt hàng " + id)); }
    public Customer customer(String id) { return db.customers.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có khách hàng " + id)); }
    public PurchaseOrder order(String id) { return db.orders.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có đơn hàng " + id)); }
    public Invoice invoice(String id) { return db.invoices.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có hóa đơn " + id)); }
    public Appointment appointment(String id) { return db.appointments.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có phiếu hẹn " + id)); }
    public Debt debt(String id) { return db.debts.stream().filter(x -> x.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Không có phiếu ghi nợ " + id)); }

    public Brand addBrand(String name) {
        name = required(name, "tên nhãn hiệu");
        final String n = name;
        require(db.brands.stream().noneMatch(x->x.name.equalsIgnoreCase(n)), "Nhãn hiệu đã tồn tại.");
        Brand b = new Brand(brandId(), name); db.brands.add(b); return b;
    }
    public Supplier addSupplier(String name, String address, String phone) {
        Supplier s = new Supplier(supplierId(), required(name,"tên nhà cung cấp"), required(address,"địa chỉ"), required(phone,"số điện thoại"));
        db.suppliers.add(s); return s;
    }
    public Product addProduct(String name, String brandId, String supplierId, String unit, long importPrice, long sellingPrice, int stock) {
        brand(brandId); supplier(supplierId);
        require(importPrice > 0 && sellingPrice > 0, "Giá nhập và giá bán phải lớn hơn 0.");
        require(stock >= 0, "Tồn kho không thể âm.");
        Product p = new Product(productId(), required(name,"tên nước giải khát"), brandId, supplierId,
                required(unit,"đơn vị tính"), importPrice, sellingPrice, stock);
        db.products.add(p); return p;
    }
    public Customer addCustomer(String name, String phone, String address, boolean regular) {
        Customer c = new Customer(customerId(), required(name,"tên khách hàng"), phone.trim(), address.trim(), regular);
        db.customers.add(c); return c;
    }
    public PurchaseOrder createOrder(String supplierId, LocalDate expected, Map<String,Integer> quantities) {
        supplier(supplierId); require(expected != null && !expected.isBefore(LocalDate.now()),"Ngày dự kiến nhận không thể trước hôm nay.");
        require(quantities != null && !quantities.isEmpty(),"Đơn hàng phải có ít nhất một mặt hàng.");
        PurchaseOrder o = new PurchaseOrder(nextId("DDH",db.orders.stream().map(x->x.id).toList()),
                supplierId,"NV001",LocalDate.now(),expected);
        for (var entry: quantities.entrySet()) {
            Product p = product(entry.getKey()); positive(entry.getValue(),"Số lượng đặt");
            require(p.supplierId.equals(supplierId), "Mặt hàng " + p.name + " không thuộc nhà cung cấp đã chọn.");
            o.lines.add(new Line(p.id,entry.getValue(),p.importPrice));
        }
        db.orders.add(o); return o;
    }
    public int ordered(PurchaseOrder order, String productId) {
        return order.lines.stream().filter(x->x.productId.equals(productId)).mapToInt(x->x.quantity).sum();
    }
    public int delivered(PurchaseOrder order, String productId) {
        return order.deliveries.stream().flatMap(x->x.lines.stream()).filter(x->x.productId.equals(productId)).mapToInt(x->x.quantity).sum();
    }
    public boolean orderCompleted(PurchaseOrder o) {
        return o.lines.stream().allMatch(x->delivered(o,x.productId) == x.quantity);
    }
    /** Một lần nhận = một đợt giao, mỗi đợt có thể có nhiều mặt hàng. */
    public Delivery receiveOrder(String orderId, LocalDate date, Map<String,Integer> quantities) {
        PurchaseOrder o = order(orderId);
        require(!orderCompleted(o),"Đơn đặt hàng đã nhận đủ.");
        require(o.deliveries.size() < 3, "Mỗi đơn đặt hàng được giao tối đa 3 đợt.");
        require(date != null && !date.isBefore(o.orderedOn),"Ngày giao không được trước ngày đặt.");
        require(quantities != null && !quantities.isEmpty(),"Chưa nhập số lượng giao.");
        List<Line> accepted = new ArrayList<>();
        for (var entry: quantities.entrySet()) {
            int qty = entry.getValue(); if (qty == 0) continue;
            positive(qty,"Số lượng giao");
            String productId = entry.getKey();
            int pending = ordered(o, productId) - delivered(o,productId);
            require(pending > 0, "Mặt hàng " + productId + " không nằm trong phần chưa nhận của đơn.");
            require(qty <= pending, "Giao vượt số lượng còn thiếu của " + productId + " (còn " + pending + ").");
            Line original = o.lines.stream().filter(x->x.productId.equals(productId)).findFirst().orElseThrow();
            accepted.add(new Line(productId,qty,original.unitPrice));
        }
        require(!accepted.isEmpty(),"Cần giao ít nhất một mặt hàng có số lượng lớn hơn 0.");
        Delivery d = new Delivery(nextId("PG", db.orders.stream().flatMap(x->x.deliveries.stream()).map(x->x.id).toList()), o.id,date,accepted);
        o.deliveries.add(d);
        for (Line line: accepted) product(line.productId).stock += line.quantity;
        return d;
    }
    /** Hóa đơn: khách vãng lai phải đủ kho, thanh toán đủ. Khách quen được hẹn giao và ghi nợ. */
    public Invoice sell(String customerId, Map<String,Integer> quantities, boolean onCredit,
                        LocalDate promiseDate, LocalDate dueDate, String reason) {
        Customer c = customer(customerId);
        require(!onCredit || c.regular, "Chỉ khách quen mới được ghi nợ.");
        require(quantities != null && !quantities.isEmpty(),"Hóa đơn chưa có mặt hàng.");
        if (onCredit) {
            require(dueDate != null && !dueDate.isBefore(LocalDate.now()),"Ngày dự kiến trả nợ không hợp lệ.");
            required(reason,"lý do ghi nợ");
        }
        List<InvoiceLine> lines = new ArrayList<>();
        boolean shortage = false;
        for (var entry: quantities.entrySet()) {
            Product p = product(entry.getKey()); int qty = positive(entry.getValue(),"Số lượng bán");
            if (qty > p.stock) shortage = true;
            require(c.regular || qty <= p.stock, "Khách vãng lai phải nhận đủ ngay. " + p.name + " chỉ còn " + p.stock + ".");
            lines.add(new InvoiceLine(p.id,qty, Math.min(qty,p.stock),p.sellingPrice));
        }
        if (shortage) require(promiseDate != null && promiseDate.isAfter(LocalDate.now()),"Khách quen thiếu hàng phải có ngày hẹn giao sau hôm nay.");
        Invoice i = new Invoice(nextId("HD", db.invoices.stream().map(x->x.id).toList()),customerId,"NV001",LocalDate.now(),!onCredit);
        i.lines.addAll(lines);
        // Sau khi toàn bộ đầu vào hợp lệ mới cập nhật tồn kho và tạo phiếu.
        for (InvoiceLine l: lines) {
            product(l.productId).stock -= l.delivered;
            if (l.delivered < l.quantity) db.appointments.add(new Appointment(
                nextId("PH",db.appointments.stream().map(x->x.id).toList()),i.id,l.productId,l.quantity-l.delivered,promiseDate));
        }
        db.invoices.add(i);
        if (onCredit) db.debts.add(new Debt(nextId("PN",db.debts.stream().map(x->x.id).toList()),
                i.id,c.id,"NV001",LocalDate.now(),dueDate,i.amount(),reason.trim()));
        return i;
    }
    /** Giao một phần hoặc toàn bộ hàng đang hẹn. */
    public void deliverAppointment(String appointmentId, int quantity) {
        Appointment a = appointment(appointmentId);
        positive(quantity,"Số lượng giao");
        require(quantity <= a.remaining(),"Số lượng giao vượt số lượng còn hẹn.");
        Product p = product(a.productId);
        require(quantity <= p.stock,"Kho chỉ còn " + p.stock + " " + p.unit + ". Hãy nhập bổ sung trước.");
        InvoiceLine line = invoice(a.invoiceId).lines.stream().filter(x->x.productId.equals(a.productId)).findFirst().orElseThrow();
        p.stock -= quantity; a.delivered += quantity; a.lastDeliveredOn=LocalDate.now();
        line.delivered += quantity;
    }
    public Appointment splitAppointment(String appointmentId, int quantityForNew, LocalDate newDate) {
        Appointment a = appointment(appointmentId);
        require(quantityForNew > 0 && quantityForNew < a.remaining(), "Số lượng tách phải lớn hơn 0 và nhỏ hơn lượng còn hẹn.");
        require(newDate != null && newDate.isAfter(a.promisedOn),"Ngày hẹn mới phải sau ngày hẹn ban đầu.");
        a.quantity -= quantityForNew;
        Appointment b = new Appointment(nextId("PH",db.appointments.stream().map(x->x.id).toList()),a.invoiceId,a.productId,quantityForNew,newDate);
        db.appointments.add(b); return b;
    }
    public void payDebt(String debtId, long amount) {
        Debt d=debt(debtId);
        require(amount > 0 && amount <= d.remaining(),"Tiền thu phải lớn hơn 0 và không được vượt số nợ còn lại.");
        d.paidAmount += amount;
    }
    public long totalSales() { return db.invoices.stream().mapToLong(Invoice::amount).sum(); }
    public long pendingDebts() { return db.debts.stream().mapToLong(Debt::remaining).sum(); }
    public long collected() { return totalSales() - pendingDebts(); }
}
