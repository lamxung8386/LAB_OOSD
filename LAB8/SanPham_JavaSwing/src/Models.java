package ngk;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Mô hình dữ liệu của cửa hàng. Tất cả số tiền lưu theo đơn vị đồng (VND). */
public final class Models {
    private Models() {}

    public static final class Brand implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, name;
        public Brand(String id, String name) { this.id = id; this.name = name; }
        public String toString() { return id + " – " + name; }
    }
    public static final class Supplier implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, name, address, phone;
        public Supplier(String id, String name, String address, String phone) {
            this.id=id; this.name=name; this.address=address; this.phone=phone;
        }
        public String toString() { return id + " – " + name; }
    }
    public static final class Product implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, name, brandId, supplierId, unit;
        public long importPrice, sellingPrice;
        public int stock;
        public Product(String id, String name, String brandId, String supplierId, String unit,
                       long importPrice, long sellingPrice, int stock) {
            this.id=id; this.name=name; this.brandId=brandId; this.supplierId=supplierId;
            this.unit=unit; this.importPrice=importPrice; this.sellingPrice=sellingPrice; this.stock=stock;
        }
        public String toString() { return id + " – " + name + " (tồn: " + stock + ")"; }
    }
    public static final class Customer implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, name, phone, address;
        public boolean regular;
        public Customer(String id, String name, String phone, String address, boolean regular) {
            this.id=id; this.name=name; this.phone=phone; this.address=address; this.regular=regular;
        }
        public String toString() { return id + " – " + name + (regular ? " (khách quen)" : " (vãng lai)"); }
    }
    public static final class Employee implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, name;
        public Employee(String id, String name) { this.id=id; this.name=name; }
    }
    public static final class Line implements Serializable {
        private static final long serialVersionUID = 1L;
        public String productId;
        public int quantity;
        public long unitPrice;
        public Line(String productId, int quantity, long unitPrice) {
            this.productId=productId; this.quantity=quantity; this.unitPrice=unitPrice;
        }
        public long amount() { return unitPrice * quantity; }
    }
    public static final class PurchaseOrder implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, supplierId, employeeId;
        public LocalDate orderedOn, expectedOn;
        public List<Line> lines = new ArrayList<>();
        public List<Delivery> deliveries = new ArrayList<>();
        public PurchaseOrder(String id, String supplierId, String employeeId,
                             LocalDate orderedOn, LocalDate expectedOn) {
            this.id=id; this.supplierId=supplierId; this.employeeId=employeeId;
            this.orderedOn=orderedOn; this.expectedOn=expectedOn;
        }
    }
    public static final class Delivery implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, orderId;
        public LocalDate date;
        public List<Line> lines = new ArrayList<>();
        public Delivery(String id, String orderId, LocalDate date, List<Line> lines) {
            this.id=id; this.orderId=orderId; this.date=date; this.lines.addAll(lines);
        }
        public long amount() { return lines.stream().mapToLong(Line::amount).sum(); }
    }
    public static final class InvoiceLine implements Serializable {
        private static final long serialVersionUID = 1L;
        public String productId;
        public int quantity, delivered;
        public long unitPrice;
        public InvoiceLine(String productId, int quantity, int delivered, long unitPrice) {
            this.productId=productId; this.quantity=quantity;
            this.delivered=delivered; this.unitPrice=unitPrice;
        }
        public long amount() { return unitPrice * quantity; }
    }
    public static final class Invoice implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, customerId, employeeId;
        public LocalDate date;
        public List<InvoiceLine> lines = new ArrayList<>();
        public boolean paidImmediately;
        public Invoice(String id, String customerId, String employeeId, LocalDate date, boolean paidImmediately) {
            this.id=id; this.customerId=customerId; this.employeeId=employeeId;
            this.date=date; this.paidImmediately=paidImmediately;
        }
        public long amount() { return lines.stream().mapToLong(InvoiceLine::amount).sum(); }
        public boolean delivered() { return lines.stream().allMatch(x -> x.delivered == x.quantity); }
    }
    public static final class Appointment implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, invoiceId, productId;
        public int quantity, delivered;
        public LocalDate promisedOn, lastDeliveredOn;
        public Appointment(String id, String invoiceId, String productId, int quantity, LocalDate promisedOn) {
            this.id=id; this.invoiceId=invoiceId; this.productId=productId;
            this.quantity=quantity; this.promisedOn=promisedOn;
        }
        public int remaining() { return quantity - delivered; }
    }
    public static final class Debt implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, invoiceId, customerId, employeeId, reason;
        public LocalDate createdOn, dueOn;
        public long amount, paidAmount;
        public Debt(String id, String invoiceId, String customerId, String employeeId,
                    LocalDate createdOn, LocalDate dueOn, long amount, String reason) {
            this.id=id; this.invoiceId=invoiceId; this.customerId=customerId; this.employeeId=employeeId;
            this.createdOn=createdOn; this.dueOn=dueOn; this.amount=amount; this.reason=reason;
        }
        public long remaining() { return amount - paidAmount; }
    }
    public static final class Database implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Brand> brands = new ArrayList<>();
        public List<Supplier> suppliers = new ArrayList<>();
        public List<Product> products = new ArrayList<>();
        public List<Customer> customers = new ArrayList<>();
        public List<Employee> employees = new ArrayList<>();
        public List<PurchaseOrder> orders = new ArrayList<>();
        public List<Invoice> invoices = new ArrayList<>();
        public List<Appointment> appointments = new ArrayList<>();
        public List<Debt> debts = new ArrayList<>();
    }
}
