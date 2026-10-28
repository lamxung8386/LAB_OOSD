package ngk;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import static ngk.Models.*;

/** Ứng dụng quản lý cửa hàng nước giải khát (Java Swing, Java 17+). */
public class Main extends JFrame {
    private final DataStore file = new DataStore(Path.of("du_lieu", "cua_hang.dat"));
    private final Database db;
    private final BusinessService service;
    private final Map<String, JTable> tables = new LinkedHashMap<>();
    private final JLabel status = new JLabel("Sẵn sàng");
    private final JLabel overview = new JLabel();
    private final JTextArea statistics = new JTextArea();
    private final NumberFormat currency = NumberFormat.getNumberInstance(new Locale("vi","VN"));

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Cần môi trường có giao diện đồ họa để chạy Java Swing."); return;
        }
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            try { new Main().setVisible(true); }
            catch (Exception ex) { JOptionPane.showMessageDialog(null,"Không mở được dữ liệu: " + ex.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE); }
        });
    }
    public Main() throws Exception {
        super("HỆ THỐNG QUẢN LÝ CỬA HÀNG NƯỚC GIẢI KHÁT");
        db = file.load();
        service = new BusinessService(db);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1270,790); setMinimumSize(new Dimension(950,640));
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(0,10));
        root.setBorder(new EmptyBorder(15,18,12,18));
        root.setBackground(new Color(245,248,252));
        JLabel title = new JLabel("QUẢN LÝ CỬA HÀNG NƯỚC GIẢI KHÁT");
        title.setFont(new Font("SansSerif",Font.BOLD,23));
        title.setForeground(new Color(23,70,120));
        JLabel note = new JLabel("Bài 2 • Phân tích thiết kế hướng đối tượng  |  Java Swing • Dữ liệu lưu tự động");
        note.setForeground(new Color(84,101,118));
        JPanel head = new JPanel(new GridLayout(2,1)); head.setOpaque(false);
        head.add(title); head.add(note); root.add(head,BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addTab("Tổng quan",makeOverview());
        tabs.addTab("Nhãn hiệu",makeTable("brands",new String[]{"Mã loại","Tên loại"},
                new String[]{"Thêm nhãn hiệu","Sửa nhãn hiệu"},new Runnable[]{this::addBrand,this::editBrand}));
        tabs.addTab("Nhà cung cấp",makeTable("suppliers",new String[]{"Mã NCC","Tên nhà cung cấp","Địa chỉ","Số điện thoại"},
                new String[]{"Thêm NCC","Sửa NCC"},new Runnable[]{this::addSupplier,this::editSupplier}));
        tabs.addTab("Nước giải khát",makeTable("products",new String[]{"Mã NGK","Tên sản phẩm","Nhãn hiệu","Nhà cung cấp","ĐVT","Giá nhập","Giá bán","Tồn kho"},
                new String[]{"Thêm NGK","Sửa thông tin"},new Runnable[]{this::addProduct,this::editProduct}));
        tabs.addTab("Khách hàng",makeTable("customers",new String[]{"Mã KH","Họ tên","Điện thoại","Địa chỉ","Loại khách"},
                new String[]{"Thêm khách","Sửa khách"},new Runnable[]{this::addCustomer,this::editCustomer}));
        tabs.addTab("Đơn nhập hàng",makeTable("orders",new String[]{"Số ĐĐH","Nhà cung cấp","Ngày đặt","Ngày nhận dự kiến","Đợt giao","Đã nhận","Tổng tiền đặt","Trạng thái"},
                new String[]{"Tạo ĐĐH","Thêm hàng vào ĐĐH","Nhận giao hàng","Xem phiếu giao"},
                new Runnable[]{this::addOrder,this::addOrderLine,this::receiveOrder,this::showOrder}));
        tabs.addTab("Hóa đơn bán",makeTable("invoices",new String[]{"Số HĐ","Khách hàng","Ngày bán","Tổng tiền","Tình trạng giao","Thanh toán"},
                new String[]{"Lập hóa đơn","Xem chi tiết"},new Runnable[]{this::addInvoice,this::showInvoice}));
        tabs.addTab("Phiếu hẹn giao",makeTable("appointments",new String[]{"Số phiếu","Số HĐ","Mặt hàng","Ngày hẹn","Số lượng hẹn","Đã giao","Còn hẹn","Trạng thái"},
                new String[]{"Giao hàng hẹn","Tách thành 2 đợt"},new Runnable[]{this::fulfillAppointment,this::splitAppointment}));
        tabs.addTab("Phiếu ghi nợ",makeTable("debts",new String[]{"Số phiếu","Số HĐ","Khách quen","Ngày lập","Hạn trả","Tiền nợ","Đã thu","Còn nợ","Trạng thái"},
                new String[]{"Thu tiền nợ","Xem phiếu nợ"},new Runnable[]{this::collectDebt,this::showDebt}));
        tabs.addTab("Thống kê",makeStatistics());
        root.add(tabs,BorderLayout.CENTER);
        status.setBorder(new EmptyBorder(6,5,4,5)); status.setForeground(new Color(89,100,114));
        root.add(status,BorderLayout.SOUTH);
        setContentPane(root); refreshAll();
    }
    private String money(long value) { return currency.format(value) + " đ"; }
    private static String idOf(JComboBox<?> combo) {
        Object o = combo.getSelectedItem();
        if(o instanceof Brand x) return x.id;
        if(o instanceof Supplier x) return x.id;
        if(o instanceof Product x) return x.id;
        if(o instanceof Customer x) return x.id;
        throw new IllegalArgumentException("Hãy chọn một mục trong danh sách.");
    }
    private static LocalDate date(JTextField field, String what) {
        try { return LocalDate.parse(field.getText().trim()); }
        catch(DateTimeParseException e) { throw new IllegalArgumentException(what + " phải có dạng yyyy-MM-dd. Ví dụ 2026-11-02."); }
    }
    private static int parseInt(JTextField field, String what) {
        try { return Integer.parseInt(field.getText().trim()); }
        catch(NumberFormatException e) { throw new IllegalArgumentException(what + " phải là số nguyên."); }
    }
    private static long parseLong(JTextField field, String what) {
        try { return Long.parseLong(field.getText().trim().replace(".","").replace(",","")); }
        catch(NumberFormatException e) { throw new IllegalArgumentException(what + " phải là số nguyên không âm, ví dụ 12000."); }
    }
    private static JTextField txt(String value) { return new JTextField(value == null ? "" : value,22); }
    private static JPanel fields(Object... elements) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(5,8,5,8); c.fill=GridBagConstraints.HORIZONTAL;
        for(int i=0;i<elements.length;i+=2) {
            c.gridx=0;c.gridy=i/2;c.weightx=0;
            p.add(new JLabel(elements[i].toString()),c);
            c.gridx=1;c.weightx=1;p.add((Component)elements[i+1],c);
        }
        return p;
    }
    private boolean confirm(String heading, Component body) {
        return JOptionPane.showConfirmDialog(this,body,heading,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
    }
    private interface Action { void run() throws Exception; }
    private void perform(String success, Action operation) {
        try {
            operation.run(); file.save(db); refreshAll();
            status.setText("Đã lưu dữ liệu • " + LocalDate.now());
            JOptionPane.showMessageDialog(this,success,"Thành công",JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage(),"Không thực hiện được",JOptionPane.WARNING_MESSAGE);
        }
    }
    private void showInfo(String heading, String details) {
        JTextArea area = new JTextArea(details,16,60); area.setEditable(false); area.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this,new JScrollPane(area),heading,JOptionPane.INFORMATION_MESSAGE);
    }
    private JTable getTable(String key) { return tables.get(key); }
    private String selected(String key) {
        JTable t=getTable(key);
        int row=t.getSelectedRow();
        if (row<0) throw new IllegalArgumentException("Hãy chọn một dòng trong bảng trước.");
        return t.getValueAt(row,0).toString();
    }
    private JPanel makeTable(String key, String[] headers, String[] commands, Runnable[] handlers) {
        JPanel panel = new JPanel(new BorderLayout(0,12)); panel.setBorder(new EmptyBorder(12,6,5,6));
        JTable table = new JTable(new DefaultTableModel(headers,0) {
            public boolean isCellEditable(int r,int c) { return false; }
        });
        table.setRowHeight(34); table.setFont(new Font("SansSerif",Font.PLAIN,13));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(231,234,237));
        tables.put(key,table);
        JScrollPane scroller = new JScrollPane(table);
        panel.add(scroller,BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,2));
        for(int i=0;i<commands.length;i++) {
            JButton btn = button(commands[i]);
            final Runnable handler=handlers[i];
            btn.addActionListener(e -> { try { handler.run(); } catch(Exception error) { JOptionPane.showMessageDialog(this,error.getMessage(),"Thông báo",JOptionPane.WARNING_MESSAGE); } });
            actions.add(btn);
        }
        JLabel tip = new JLabel("Chọn dòng trong bảng trước khi sửa, xem hoặc xử lý phiếu.");
        tip.setForeground(new Color(112,122,134)); actions.add(tip);
        panel.add(actions,BorderLayout.NORTH);
        return panel;
    }
    private JButton button(String caption) {
        JButton b=new JButton(caption); b.setFont(new Font("SansSerif",Font.BOLD,12));
        b.setFocusPainted(false); b.setBackground(new Color(223,235,247));
        return b;
    }
    private JPanel makeOverview() {
        JPanel p=new JPanel(new BorderLayout(10,15));p.setBorder(new EmptyBorder(20,14,16,14));
        overview.setVerticalAlignment(SwingConstants.TOP);
        p.add(overview,BorderLayout.NORTH);
        JTextArea help=new JTextArea("QUY TRÌNH DEMO\n\n"+
            "1. Vào Nước giải khát để xem tồn kho và nhà cung cấp của từng mặt hàng.\n"+
            "2. Vào Đơn nhập hàng → chọn DDH001 → Nhận giao hàng. Mỗi đơn giao tối đa 3 đợt.\n"+
            "3. Vào Hóa đơn bán → Lập hóa đơn. Khách vãng lai phải giao và thu tiền ngay.\n"+
            "4. Chọn khách quen, mua nhiều hơn tồn kho để tự tạo phiếu hẹn giao.\n"+
            "5. Với khách quen, chọn Ghi nợ để lập phiếu ghi nợ và dự kiến ngày trả.\n"+
            "6. Vào Phiếu hẹn giao để giao đủ hàng khi tồn kho cho phép; vào Phiếu ghi nợ để thu nợ.\n\n"+
            "Dữ liệu được lưu tự động trong thư mục du_lieu, khởi động lại không mất dữ liệu.");
        help.setFont(new Font("SansSerif",Font.PLAIN,15));help.setEditable(false); help.setLineWrap(true);
        help.setWrapStyleWord(true);help.setBackground(new Color(247,249,252));
        help.setBorder(new EmptyBorder(25,25,25,20));
        p.add(help,BorderLayout.CENTER);
        return p;
    }
    private JPanel makeStatistics() {
        JPanel p=new JPanel(new BorderLayout(8,8)); p.setBorder(new EmptyBorder(14,12,10,12));
        statistics.setEditable(false); statistics.setFont(new Font(Font.MONOSPACED,Font.PLAIN,15));
        JButton reload=button("Làm mới thống kê"); reload.addActionListener(e -> refreshAll());
        p.add(reload,BorderLayout.NORTH);p.add(new JScrollPane(statistics),BorderLayout.CENTER);
        return p;
    }
    private static Object[] arr(Object... objects) { return objects; }
    private void rows(String key, List<Object[]> rows) {
        JTable table=tables.get(key);
        if(table==null) return;
        String[] cols = new String[table.getColumnCount()];
        for(int k=0;k<cols.length;k++) cols[k]=table.getColumnName(k);
        DefaultTableModel model=new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c) { return false; } };
        for(Object[] row:rows) model.addRow(row);
        table.setModel(model);
    }
    private static <T> List<Object[]> map(Collection<T> list, java.util.function.Function<T,Object[]> convert) {
        List<Object[]> result=new ArrayList<>(); for(T item:list) result.add(convert.apply(item)); return result;
    }
    private String productName(String id) { return service.product(id).name; }
    private String supplierName(String id) { return service.supplier(id).name; }
    private String customerName(String id) { return service.customer(id).name; }
    private void refreshAll() {
        rows("brands",map(db.brands,x->arr(x.id,x.name)));
        rows("suppliers",map(db.suppliers,x->arr(x.id,x.name,x.address,x.phone)));
        rows("products",map(db.products,x->arr(x.id,x.name,service.brand(x.brandId).name,supplierName(x.supplierId),x.unit,money(x.importPrice),money(x.sellingPrice),x.stock)));
        rows("customers",map(db.customers,x->arr(x.id,x.name,x.phone,x.address,x.regular?"Khách quen":"Khách vãng lai")));
        rows("orders",map(db.orders,x->arr(x.id,supplierName(x.supplierId),x.orderedOn,x.expectedOn,x.deliveries.size()+"/3",
            x.lines.stream().mapToInt(l->service.delivered(x,l.productId)).sum()+"/"+x.lines.stream().mapToInt(l->l.quantity).sum(),
            money(x.lines.stream().mapToLong(Line::amount).sum()),service.orderCompleted(x)?"Đã nhận đủ":"Đang nhận")));
        rows("invoices",map(db.invoices,x->arr(x.id,customerName(x.customerId),x.date,money(x.amount()),
                x.delivered()?"Đã giao đủ":"Đang hẹn giao",x.paidImmediately?"Đã thanh toán":"Ghi nợ")));
        rows("appointments",map(db.appointments,x->arr(x.id,x.invoiceId,productName(x.productId),x.promisedOn,x.quantity,x.delivered,x.remaining(),
                x.remaining()==0?"Đã giao đủ":"Chưa giao đủ")));
        rows("debts",map(db.debts,x->arr(x.id,x.invoiceId,customerName(x.customerId),x.createdOn,x.dueOn,
                money(x.amount),money(x.paidAmount),money(x.remaining()),x.remaining()==0?"Đã trả đủ":"Còn nợ")));
        overview.setText("<html><table cellpadding='14' bgcolor='#EEF6FF'><tr>"+
            card("MẶT HÀNG",String.valueOf(db.products.size()))+
            card("TỔNG HÓA ĐƠN",String.valueOf(db.invoices.size()))+
            card("DOANH SỐ",money(service.totalSales()))+
            card("CÒN PHẢI THU",money(service.pendingDebts()))+
            "</tr></table></html>");
        StringBuilder b=new StringBuilder();
        b.append("THỐNG KÊ CỬA HÀNG\n").append("=".repeat(70)).append("\n\n");
        b.append("Số nhãn hiệu: ").append(db.brands.size()).append("\n");
        b.append("Số nhà cung cấp: ").append(db.suppliers.size()).append("\n");
        b.append("Số nước giải khát: ").append(db.products.size()).append("\n");
        b.append("Số khách hàng: ").append(db.customers.size()).append("\n");
        b.append("Số đơn nhập hàng: ").append(db.orders.size()).append("\n");
        b.append("Số hóa đơn: ").append(db.invoices.size()).append("\n");
        b.append("Tổng doanh số: ").append(money(service.totalSales())).append("\n");
        b.append("Đã thu từ bán hàng: ").append(money(service.collected())).append("\n");
        b.append("Tổng số còn nợ: ").append(money(service.pendingDebts())).append("\n\n");
        b.append("MẶT HÀNG SẮP HẾT (TỒN <= 10)\n").append("-".repeat(70)).append("\n");
        for(Product x:db.products) if(x.stock<=10) b.append(String.format("%-9s %-31s : %d %s\n",x.id,x.name,x.stock,x.unit));
        b.append("\nPHIẾU HẸN CHƯA HOÀN THÀNH\n").append("-".repeat(70)).append("\n");
        for(Appointment x:db.appointments) if(x.remaining()>0) b.append(x.id).append(" | ").append(x.invoiceId).append(" | ").append(productName(x.productId))
                .append(" | còn: ").append(x.remaining()).append(" | hẹn: ").append(x.promisedOn).append("\n");
        b.append("\nNỢ CHƯA THU\n").append("-".repeat(70)).append("\n");
        for(Debt x:db.debts) if(x.remaining()>0) b.append(x.id).append(" | ").append(customerName(x.customerId))
                .append(" | ").append(money(x.remaining())).append(" | đến hạn: ").append(x.dueOn).append("\n");
        statistics.setText(b.toString());statistics.setCaretPosition(0);
    }
    private String card(String heading,String value) {
        return "<td width='230'><font color='#53718B'>" + heading + "</font><br><font size='+2' color='#0E528A'><b>" + value + "</b></font></td>";
    }

    // ---------- Danh mục ----------
    private void addBrand() {
        JTextField name=txt(""); if(!confirm("Thêm nhãn hiệu",fields("Tên nhãn hiệu:",name))) return;
        perform("Đã thêm nhãn hiệu.",()->service.addBrand(name.getText()));
    }
    private void editBrand() {
        Brand b=service.brand(selected("brands")); JTextField name=txt(b.name);
        if(!confirm("Sửa nhãn hiệu " + b.id,fields("Tên nhãn hiệu:",name))) return;
        perform("Đã cập nhật.",()->{ b.name=BusinessService.required(name.getText(),"tên nhãn hiệu"); });
    }
    private void addSupplier() {
        JTextField name=txt(""), address=txt(""), phone=txt("");
        if(!confirm("Thêm nhà cung cấp",fields("Tên nhà cung cấp:",name,"Địa chỉ:",address,"Số điện thoại:",phone))) return;
        perform("Đã thêm nhà cung cấp.",()->service.addSupplier(name.getText(),address.getText(),phone.getText()));
    }
    private void editSupplier() {
        Supplier s=service.supplier(selected("suppliers"));
        JTextField name=txt(s.name),address=txt(s.address),phone=txt(s.phone);
        if(!confirm("Sửa nhà cung cấp " + s.id,fields("Tên NCC:",name,"Địa chỉ:",address,"Số điện thoại:",phone))) return;
        perform("Đã cập nhật.",()->{ String n=BusinessService.required(name.getText(),"tên NCC");
            String a=BusinessService.required(address.getText(),"địa chỉ");
            String p=BusinessService.required(phone.getText(),"điện thoại");s.name=n;s.address=a;s.phone=p; });
    }
    private void addProduct() {
        productDialog(null);
    }
    private void editProduct() {
        productDialog(service.product(selected("products")));
    }
    private void productDialog(Product old) {
        JTextField name=txt(old==null?"":old.name),unit=txt(old==null?"Lon":old.unit);
        JTextField cost=txt(old==null?"7000":String.valueOf(old.importPrice));
        JTextField price=txt(old==null?"10000":String.valueOf(old.sellingPrice));
        JTextField stock=txt(old==null?"0":String.valueOf(old.stock));
        JComboBox<Brand> brands=new JComboBox<>(db.brands.toArray(new Brand[0]));
        JComboBox<Supplier> suppliers=new JComboBox<>(db.suppliers.toArray(new Supplier[0]));
        if(old!=null) {
            brands.setSelectedItem(db.brands.stream().filter(b->b.id.equals(old.brandId)).findFirst().orElse(null));
            suppliers.setSelectedItem(db.suppliers.stream().filter(s->s.id.equals(old.supplierId)).findFirst().orElse(null));
        }
        JPanel form=fields("Tên mặt hàng:",name,"Nhãn hiệu:",brands,"Nhà cung cấp:",suppliers,
                "Đơn vị tính:",unit,"Giá nhập (đ):",cost,"Giá bán (đ):",price,"Tồn kho ban đầu:",stock);
        if(!confirm(old==null?"Thêm nước giải khát":"Sửa mặt hàng " + old.id,form))return;
        perform("Đã lưu thông tin mặt hàng.",()->{
            long c=parseLong(cost,"Giá nhập"),p=parseLong(price,"Giá bán"); int n=parseInt(stock,"Tồn kho");
            if(old==null) service.addProduct(name.getText(),idOf(brands),idOf(suppliers),unit.getText(),c,p,n);
            else {
                BusinessService.required(name.getText(),"tên mặt hàng"); BusinessService.required(unit.getText(),"đơn vị tính");
                BusinessService.require(c>0 && p>0 && n>=0,"Giá phải dương, tồn kho không âm.");
                // Mặt hàng không được tự ý đổi NCC sau khi đã nằm trong đơn nhập hàng.
                boolean imported=db.orders.stream().anyMatch(o->o.lines.stream().anyMatch(l->l.productId.equals(old.id)));
                BusinessService.require(!imported || old.supplierId.equals(idOf(suppliers)),"Mặt hàng đã nằm trong đơn nhập, không được chuyển NCC.");
                old.name=name.getText().trim();old.unit=unit.getText().trim();old.brandId=idOf(brands);
                old.supplierId=idOf(suppliers);old.importPrice=c;old.sellingPrice=p;old.stock=n;
            }
        });
    }
    private void addCustomer() { customerDialog(null); }
    private void editCustomer() { customerDialog(service.customer(selected("customers"))); }
    private void customerDialog(Customer old) {
        JTextField name=txt(old==null?"":old.name),phone=txt(old==null?"":old.phone),address=txt(old==null?"":old.address);
        JCheckBox regular=new JCheckBox("Khách quen (được hẹn giao hàng và ghi nợ)",old!=null && old.regular);
        if(!confirm(old==null?"Thêm khách hàng":"Sửa khách hàng " + old.id,
                fields("Họ và tên:",name,"Điện thoại:",phone,"Địa chỉ:",address,"Phân loại:",regular)))return;
        perform("Đã lưu khách hàng.",()->{
            if(old==null) service.addCustomer(name.getText(),phone.getText(),address.getText(),regular.isSelected());
            else {
                old.name=BusinessService.required(name.getText(),"tên khách hàng");
                old.phone=phone.getText().trim();old.address=address.getText().trim();old.regular=regular.isSelected();
            }
        });
    }

    // ---------- Nhập hàng ----------
    private Map<String,Integer> qtyForm(String supplierId, List<Product> source, String title, boolean includeStock) {
        JPanel p=new JPanel(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();c.insets=new Insets(5,9,5,9);c.anchor=GridBagConstraints.WEST;
        LinkedHashMap<String,JSpinner> controls=new LinkedHashMap<>();
        int row=0;
        for(Product x:source) {
            if(supplierId!=null && !x.supplierId.equals(supplierId)) continue;
            c.gridy=row;c.gridx=0;
            p.add(new JLabel(x.id+" – "+x.name+(includeStock?" | tồn " +x.stock:"")),c);
            c.gridx=1;
            JSpinner qty=new JSpinner(new SpinnerNumberModel(0,0,100000,1));
            p.add(qty,c); controls.put(x.id,qty); row++;
        }
        if(controls.isEmpty()) throw new IllegalArgumentException("Không có mặt hàng phù hợp. Hãy thêm nước giải khát trước.");
        JScrollPane view=new JScrollPane(p);view.setPreferredSize(new Dimension(600,Math.min(410,90+row*43)));
        if(!confirm(title,view))return null;
        Map<String,Integer> chosen=new LinkedHashMap<>();
        controls.forEach((key,sp)->{int n=(Integer)sp.getValue();if(n>0)chosen.put(key,n);});
        return chosen;
    }
    private void addOrder() {
        JComboBox<Supplier> combo=new JComboBox<>(db.suppliers.toArray(new Supplier[0]));
        JTextField expected=txt(LocalDate.now().plusDays(3).toString());
        if(!confirm("Tạo đơn đặt hàng",fields("Nhà cung cấp:",combo,"Ngày dự kiến nhận (yyyy-MM-dd):",expected)))return;
        try {
            LocalDate d=date(expected,"Ngày dự kiến nhận");
            Map<String,Integer> quantities=qtyForm(idOf(combo),db.products,"Nhập số lượng cần đặt (bỏ 0 nếu không đặt)",false);
            if(quantities==null)return;
            perform("Đã tạo đơn đặt hàng.",()->service.createOrder(idOf(combo),d,quantities));
        } catch(Exception e) { JOptionPane.showMessageDialog(this,e.getMessage(),"Lỗi",JOptionPane.WARNING_MESSAGE); }
    }
    private void addOrderLine() {
        PurchaseOrder o=service.order(selected("orders"));
        BusinessService.require(o.deliveries.isEmpty(),"Đã có giao hàng, không thể bổ sung mặt hàng vào đơn này.");
        List<Product> candidates=db.products.stream().filter(x->x.supplierId.equals(o.supplierId))
                .filter(x->o.lines.stream().noneMatch(y->y.productId.equals(x.id))).toList();
        Map<String,Integer> data=qtyForm(o.supplierId,candidates,"Bổ sung mặt hàng cho " + o.id,false);
        if(data==null)return;
        perform("Đã bổ sung mặt hàng.",()->{
            BusinessService.require(!data.isEmpty(),"Chưa nhập mặt hàng cần bổ sung.");
            for(var entry:data.entrySet()){
                Product p=service.product(entry.getKey());
                o.lines.add(new Line(p.id,entry.getValue(),p.importPrice));
            }
        });
    }
    private void receiveOrder() {
        PurchaseOrder o=service.order(selected("orders"));
        BusinessService.require(!service.orderCompleted(o),"Đơn này đã nhận đủ.");
        BusinessService.require(o.deliveries.size()<3,"Đơn này đã giao đủ 3 đợt, không thể lập thêm phiếu giao.");
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints(); c.insets=new Insets(6,8,6,8); c.anchor=GridBagConstraints.WEST;
        JTextField dateField=txt(LocalDate.now().toString()); c.gridx=0;c.gridy=0;p.add(new JLabel("Ngày giao (yyyy-MM-dd):"),c);
        c.gridx=1;p.add(dateField,c);
        Map<String,JSpinner> quantities=new LinkedHashMap<>();int row=1;
        for(Line line:o.lines) {
            int remaining=line.quantity-service.delivered(o,line.productId); if(remaining==0)continue;
            c.gridy=row;c.gridx=0;
            p.add(new JLabel(productName(line.productId)+" | còn thiếu: " + remaining),c);
            c.gridx=1;JSpinner qty=new JSpinner(new SpinnerNumberModel(0,0,remaining,1));
            p.add(qty,c); quantities.put(line.productId,qty);row++;
        }
        if(!confirm("Nhận đợt giao thứ " +(o.deliveries.size()+1)+" của " +o.id,p))return;
        Map<String,Integer> data=new LinkedHashMap<>();
        quantities.forEach((id,sp)->{int n=(Integer)sp.getValue();if(n>0)data.put(id,n);});
        perform("Đã lập phiếu giao hàng và cập nhật tồn kho.",()->service.receiveOrder(o.id,date(dateField,"Ngày giao"),data));
    }
    private void showOrder() {
        PurchaseOrder o=service.order(selected("orders"));
        StringBuilder b=new StringBuilder("ĐƠN ĐẶT HÀNG "+o.id+"\nNhà cung cấp: "+supplierName(o.supplierId)+"\nNgày đặt: "+o.orderedOn+"\nDự kiến nhận: "+o.expectedOn+"\n\n");
        for(Line x:o.lines) b.append(productName(x.productId)).append(" | đặt: ").append(x.quantity)
                .append(" | nhận: ").append(service.delivered(o,x.productId)).append(" | giá nhập: ").append(money(x.unitPrice)).append("\n");
        b.append("\nCÁC PHIẾU GIAO (tối đa 3)\n");
        if(o.deliveries.isEmpty())b.append("Chưa giao đợt nào.\n");
        for(Delivery d:o.deliveries) {
            b.append(d.id).append(" | ngày ").append(d.date).append(" | số tiền: ").append(money(d.amount())).append("\n");
            for(Line l:d.lines)b.append("  • ").append(productName(l.productId)).append(" × ").append(l.quantity).append("\n");
        }
        showInfo("Chi tiết đơn nhập hàng",b.toString());
    }

    // ---------- Bán hàng ----------
    private void addInvoice() {
        JComboBox<Customer> customerBox=new JComboBox<>(db.customers.toArray(new Customer[0]));
        JCheckBox onCredit=new JCheckBox("Ghi nợ (chỉ khách quen)");
        JTextField promise=txt(LocalDate.now().plusDays(2).toString());
        JTextField due=txt(LocalDate.now().plusDays(14).toString());
        JTextField reason=txt("Khách quen xin thanh toán sau");
        JPanel info=fields("Khách hàng:",customerBox,"Thanh toán:",onCredit,
                "Ngày hẹn giao nếu thiếu (yyyy-MM-dd):",promise,
                "Ngày dự kiến trả nợ (yyyy-MM-dd):",due,"Lý do ghi nợ:",reason);
        info.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        JPanel lines=new JPanel(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();c.insets=new Insets(4,8,4,8);c.anchor=GridBagConstraints.WEST;
        LinkedHashMap<String,JSpinner> controls=new LinkedHashMap<>();int row=0;
        for(Product p:db.products) {
            c.gridy=row;c.gridx=0;
            lines.add(new JLabel(p.id+" - "+p.name+" | Giá "+money(p.sellingPrice)+" | Tồn "+p.stock),c);
            c.gridx=1;JSpinner sp=new JSpinner(new SpinnerNumberModel(0,0,100000,1));
            controls.put(p.id,sp);lines.add(sp,c);row++;
        }
        if(controls.isEmpty())throw new IllegalArgumentException("Chưa có nước giải khát để bán.");
        JPanel dialog=new JPanel(new BorderLayout(5,10));dialog.add(info,BorderLayout.NORTH);
        JScrollPane scroll=new JScrollPane(lines);scroll.setBorder(BorderFactory.createTitledBorder("Nhập số lượng mua (0 = bỏ qua)"));
        scroll.setPreferredSize(new Dimension(730,Math.min(280,75+row*45)));dialog.add(scroll,BorderLayout.CENTER);
        if(!confirm("Lập hóa đơn bán hàng",dialog))return;
        Map<String,Integer> quantities=new LinkedHashMap<>();
        controls.forEach((k,v)->{int n=(Integer)v.getValue();if(n>0)quantities.put(k,n);});
        perform("Lập hóa đơn thành công. Kiểm tra các phiếu hẹn hoặc phiếu ghi nợ nếu có.",()->{
            boolean shortage=quantities.entrySet().stream().anyMatch(x->x.getValue()>service.product(x.getKey()).stock);
            service.sell(idOf(customerBox),quantities,onCredit.isSelected(),
                shortage?date(promise,"Ngày hẹn giao"):null,
                onCredit.isSelected()?date(due,"Ngày dự kiến trả"):null,reason.getText());
        });
    }
    private void showInvoice() {
        Invoice i=service.invoice(selected("invoices"));
        StringBuilder b=new StringBuilder("HÓA ĐƠN "+i.id+"\nKhách: "+customerName(i.customerId)+"\nNgày: "+i.date+"\nThanh toán: "+
                (i.paidImmediately?"Đã thanh toán ngay":"Ghi nợ")+"\n\n");
        for(InvoiceLine l:i.lines) b.append(productName(l.productId)).append(" × ").append(l.quantity)
                .append(" | đã giao ").append(l.delivered).append(" | giá bán ").append(money(l.unitPrice))
                .append(" | thành tiền ").append(money(l.amount())).append("\n");
        b.append("\nTỔNG CỘNG: ").append(money(i.amount())).append("\n");
        b.append("Tình trạng: ").append(i.delivered()?"Đã giao đủ":"Đang hẹn giao");
        showInfo("Chi tiết hóa đơn",b.toString());
    }
    private void fulfillAppointment() {
        Appointment a=service.appointment(selected("appointments"));
        BusinessService.require(a.remaining()>0,"Phiếu hẹn đã giao đủ.");
        JTextField quantity=txt(String.valueOf(Math.min(a.remaining(),service.product(a.productId).stock)));
        if(!confirm("Giao hàng theo phiếu " +a.id,fields("Mặt hàng:",new JLabel(productName(a.productId)),
                "Còn phải giao:",new JLabel(String.valueOf(a.remaining())),
                "Tồn kho:",new JLabel(String.valueOf(service.product(a.productId).stock)),
                "Số lượng giao lần này:",quantity)))return;
        perform("Đã giao hàng và cập nhật hóa đơn, tồn kho.",()->service.deliverAppointment(a.id,parseInt(quantity,"Số lượng giao")));
    }
    private void splitAppointment() {
        Appointment a=service.appointment(selected("appointments"));
        JTextField quantity=txt("1"),nextDate=txt(a.promisedOn.plusDays(2).toString());
        if(!confirm("Tách phiếu hẹn " +a.id,fields("Số lượng chuyển sang đợt mới:",quantity,
                "Ngày hẹn đợt mới (yyyy-MM-dd):",nextDate)))return;
        perform("Đã chia phiếu hẹn thành hai đợt.",()->service.splitAppointment(a.id,parseInt(quantity,"Số lượng tách"),date(nextDate,"Ngày hẹn mới")));
    }
    private void collectDebt() {
        Debt d=service.debt(selected("debts"));
        BusinessService.require(d.remaining()>0,"Phiếu nợ đã được thanh toán đủ.");
        JTextField amount=txt(String.valueOf(d.remaining()));
        if(!confirm("Thu tiền cho phiếu " +d.id,fields("Khách hàng:",new JLabel(customerName(d.customerId)),
                "Số tiền còn nợ:",new JLabel(money(d.remaining())),
                "Số tiền thực thu (đ):",amount)))return;
        perform("Đã ghi nhận khoản tiền thu nợ.",()->service.payDebt(d.id,parseLong(amount,"Tiền thu nợ")));
    }
    private void showDebt() {
        Debt d=service.debt(selected("debts"));
        showInfo("Chi tiết phiếu ghi nợ", "PHIẾU GHI NỢ "+d.id+"\nHóa đơn: "+d.invoiceId+"\nKhách quen: "+
            customerName(d.customerId)+"\nNhân viên lập: "+d.employeeId+"\nNgày lập: "+d.createdOn+"\nNgày dự kiến trả: "+d.dueOn+
            "\nLý do: "+d.reason+"\n\nSố tiền nợ: "+money(d.amount)+"\nĐã trả: "+money(d.paidAmount)+"\nCòn nợ: "+money(d.remaining()));
    }
}
