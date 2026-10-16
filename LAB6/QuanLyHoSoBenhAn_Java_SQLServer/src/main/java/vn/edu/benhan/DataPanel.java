package vn.edu.benhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/** CRUD screen generated from a fixed, vetted entity definition. */
public final class DataPanel extends JPanel {
    public record Option(Integer id, String label) { @Override public String toString() { return label; } }
    private final Specs.Spec spec;
    private final boolean canWrite;
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final LinkedHashMap<String, JComponent> inputs = new LinkedHashMap<>();
    private final JTextField search = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;
    private Integer selectedId;

    public DataPanel(Specs.Spec spec, Auth.User user) {
        this.spec = spec;
        this.canWrite = Auth.canEdit(user, spec.key());
        setLayout(new BorderLayout(14, 14)); setBackground(Theme.BG);
        setBorder(new EmptyBorder(18, 20, 20, 20));
        JPanel heading = new JPanel(new BorderLayout(10, 6)); heading.setOpaque(false);
        JPanel titles = new JPanel(new GridLayout(2, 1)); titles.setOpaque(false);
        titles.add(Theme.title(spec.title()));
        titles.add(Theme.caption("Lưu dữ liệu bằng SQL Server  •  Chọn một dòng để xem hoặc chỉnh sửa"));
        heading.add(titles, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); right.setOpaque(false);
        search.setColumns(17); search.setToolTipText("Tìm theo bất kỳ cột nào");
        right.add(new JLabel("Tìm kiếm:")); right.add(search);
        JButton refresh = Theme.button("Làm mới");
        refresh.addActionListener(e -> reload()); right.add(refresh); heading.add(right, BorderLayout.EAST);
        add(heading, BorderLayout.NORTH);

        JPanel center = Theme.card(new BorderLayout());
        table.setModel(model); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true); table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) selectRow();
        });
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        buildForm();
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        reload();
    }

    private void buildForm() {
        JPanel card = Theme.card(new BorderLayout(8, 10));
        card.setPreferredSize(new Dimension(320, 100));
        JLabel h = new JLabel("Thông tin chi tiết");
        h.setFont(new Font("Segoe UI", Font.BOLD, 16)); h.setForeground(Theme.NAVY);
        card.add(h, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridBagLayout()); grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints(); g.gridx=0; g.weightx=1;
        g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(4,0,4,0);
        int row=0;
        for (Specs.Field f : spec.fields()) {
            g.gridy=row++; JLabel label = new JLabel(f.title()); label.setForeground(Theme.TEXT); grid.add(label,g);
            g.gridy=row++;
            JComponent editor = f.type().equals("FK") || f.type().equals("CHOICE") ? new JComboBox<>() : new JTextField();
            if (editor instanceof JTextField tf) { tf.setPreferredSize(new Dimension(200, 29)); }
            if (editor instanceof JComboBox<?> cb) { cb.setPreferredSize(new Dimension(200, 29)); }
            inputs.put(f.column(), editor);
            grid.add(editor,g);
        }
        JScrollPane sc = new JScrollPane(grid); sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getVerticalScrollBar().setUnitIncrement(17);
        card.add(sc,BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(2,2,7,7)); actions.setOpaque(false);
        JButton add = Theme.button("Thêm"), update=Theme.button("Sửa"),
                delete=Theme.button("Xóa"), clear=Theme.button("Nhập mới");
        add.setEnabled(canWrite && spec.allowAdd()); update.setEnabled(canWrite && spec.allowUpdate());
        delete.setEnabled(canWrite && spec.allowDelete());
        add.addActionListener(e -> change(false)); update.addActionListener(e -> change(true));
        delete.addActionListener(e -> delete()); clear.addActionListener(e -> clear());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        JPanel bottom = new JPanel(new BorderLayout(5,6)); bottom.setOpaque(false);
        if (!canWrite || !spec.allowAdd() && !spec.allowUpdate()) {
            JLabel label = Theme.caption("Màn hình này chỉ cho phép xem (hoặc theo phân quyền).");
            bottom.add(label, BorderLayout.NORTH);
        }
        bottom.add(actions,BorderLayout.SOUTH); card.add(bottom,BorderLayout.SOUTH);
        add(card,BorderLayout.EAST);
    }

    private void reload() {
        try {
            loadOptions();
            ArrayList<Object[]> data=new ArrayList<>();
            try(Connection c=Db.open(); Statement s=c.createStatement();
                ResultSet rs=s.executeQuery("SELECT * FROM dbo."+spec.table()+" ORDER BY "+spec.pk()+" DESC")) {
                ResultSetMetaData md=rs.getMetaData(); int n=md.getColumnCount();
                String[] names=new String[n]; for(int i=1;i<=n;i++)names[i-1]=md.getColumnLabel(i);
                while(rs.next()) {
                    Object[] r=new Object[n];
                    for(int i=1;i<=n;i++)r[i-1]=rs.getObject(i);
                    data.add(r);
                }
                model.setDataVector(data.toArray(new Object[0][]),names);
            }
            table.setRowHeight(29);
            for(int i=0;i<table.getColumnCount();i++)
                table.getColumnModel().getColumn(i).setPreferredWidth(i==0?72:155);
            sorter=new TableRowSorter<>(model);table.setRowSorter(sorter);
            filter();
            clear();
        } catch (Exception ex) { Theme.error(this,ex); }
    }

    private void loadOptions() throws SQLException {
        for(Specs.Field f:spec.fields()) {
            JComponent c=inputs.get(f.column());
            if(f.type().equals("CHOICE")) {
                @SuppressWarnings("unchecked") JComboBox<String> cb=(JComboBox<String>)c;
                cb.removeAllItems(); for(String val:f.values().split("\\|")) cb.addItem(val);
            } else if(f.type().equals("FK")) {
                @SuppressWarnings("unchecked") JComboBox<Option> cb=(JComboBox<Option>)c;
                cb.removeAllItems(); cb.addItem(new Option(null,"-- Chọn --"));
                String[] v=f.values().split(":");
                try(Connection conn=Db.open();Statement stmt=conn.createStatement();
                    ResultSet rs=stmt.executeQuery("SELECT "+v[1]+", "+v[2]+" FROM dbo."+v[0]+" ORDER BY "+v[1])){
                    while(rs.next())cb.addItem(new Option(rs.getInt(1),rs.getInt(1)+" - "+rs.getString(2)));
                }
            }
        }
    }

    private void filter() {
        if(sorter!=null)sorter.setRowFilter(RowFilter.regexFilter("(?iu)"+Pattern.quote(search.getText().trim())));
    }

    private void clear() {
        selectedId=null;
        table.clearSelection();
        for(Specs.Field f:spec.fields()) {
            JComponent comp=inputs.get(f.column());
            if(comp instanceof JTextField tf) {
                tf.setText(switch(f.type()) {
                    case "DATE" -> LocalDate.now().toString();
                    case "DATETIME" -> LocalDateTime.now().withSecond(0).withNano(0).toString().replace('T',' ');
                    default -> "";
                });
            } else if(comp instanceof JComboBox<?> cb && cb.getItemCount()>0)cb.setSelectedIndex(0);
        }
    }

    private void selectRow() {
        try {
            int r=table.convertRowIndexToModel(table.getSelectedRow());
            selectedId = Integer.parseInt(model.getValueAt(r,model.findColumn(spec.pk())).toString());
            for(Specs.Field f:spec.fields()) {
                int col=model.findColumn(f.column()); if(col<0)continue;
                Object value=model.getValueAt(r,col);
                JComponent comp=inputs.get(f.column());
                if(comp instanceof JTextField tf) tf.setText(value==null?"":value.toString());
                else if(comp instanceof JComboBox<?> cb) {
                    if(f.type().equals("CHOICE")) cb.setSelectedItem(value==null?null:value.toString());
                    else if(value!=null) {
                        for(int j=0;j<cb.getItemCount();j++){
                            Option o=(Option)cb.getItemAt(j);
                            if(o.id()!=null&&o.id()==((Number)value).intValue()) {cb.setSelectedIndex(j);break;}
                        }
                    } else cb.setSelectedIndex(0);
                }
            }
        } catch(Exception ex){Theme.error(this,ex);}
    }

    private Object getValue(Specs.Field f) {
        JComponent comp=inputs.get(f.column());
        if(comp instanceof JComboBox<?> cb) {
            if(f.type().equals("CHOICE"))return cb.getSelectedItem();
            Option option=(Option)cb.getSelectedItem(); return option==null?null:option.id();
        }
        String raw=((JTextField)comp).getText().trim(); if(raw.isEmpty())return null;
        return switch(f.type()) {
            case "DATE" -> java.sql.Date.valueOf(raw);
            case "DATETIME" -> Timestamp.valueOf(raw.length()==16?raw+":00":raw.replace('T',' '));
            case "INT" -> Integer.parseInt(raw);
            case "MONEY" -> new BigDecimal(raw);
            default -> raw;
        };
    }

    private void change(boolean update) {
        if(!canWrite||update&&!spec.allowUpdate()||!update&&!spec.allowAdd())return;
        if(update && selectedId==null){Theme.info(this,"Hãy chọn một dòng trong bảng để sửa.");return;}
        try {
            // Medical records become immutable once archived via the payment workflow.
            if(spec.key().equals("PHIEUKHAM")) {
                JComboBox<?> statusBox=(JComboBox<?>)inputs.get("TrangThai");
                if("Đã lưu".equals(statusBox.getSelectedItem()))
                    throw new IllegalArgumentException("Không thể tự đặt 'Đã lưu'; hãy sử dụng Xuất viện + thanh toán.");
                if(update)try(Connection conn=Db.open();
                              PreparedStatement check=conn.prepareStatement("SELECT TrangThai FROM dbo.PhieuKham WHERE MaPK=?")){
                    check.setInt(1,selectedId);
                    try(ResultSet rs=check.executeQuery()){
                        if(rs.next() && "Đã lưu".equals(rs.getString(1)))
                            throw new IllegalArgumentException("Phiếu khám đã lưu không được chỉnh sửa.");
                    }
                }
            }
            List<Specs.Field> fields=spec.fields();
            StringBuilder sql=new StringBuilder();
            if(update) {
                sql.append("UPDATE dbo.").append(spec.table()).append(" SET ");
                for(int i=0;i<fields.size();i++) {
                    if(i>0)sql.append(",");sql.append(fields.get(i).column()).append("=?");
                }
                sql.append(" WHERE ").append(spec.pk()).append("=?");
            } else {
                sql.append("INSERT dbo.").append(spec.table()).append(" (");
                sql.append(String.join(",",fields.stream().map(Specs.Field::column).toList()));
                sql.append(") VALUES (");
                sql.append(String.join(",",Collections.nCopies(fields.size(),"?"))).append(")");
            }
            try(Connection c=Db.open();PreparedStatement p=c.prepareStatement(sql.toString())) {
                for(int i=0;i<fields.size();i++)p.setObject(i+1,getValue(fields.get(i)));
                if(update)p.setInt(fields.size()+1,selectedId);
                int rows=p.executeUpdate();if(rows==0)throw new SQLException("Không có bản ghi được cập nhật.");
            }
            Theme.info(this,update?"Đã cập nhật thành công.":"Đã thêm thành công.");reload();
        } catch(Exception ex){Theme.error(this,ex);}
    }

    private void delete(){
        if(!canWrite||!spec.allowDelete())return;
        if(selectedId==null){Theme.info(this,"Hãy chọn dòng cần xóa.");return;}
        if(JOptionPane.showConfirmDialog(this,"Xóa bản ghi mã " + selectedId+"?",
                "Xác nhận",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try(Connection c=Db.open();PreparedStatement p=c.prepareStatement(
                "DELETE FROM dbo."+spec.table()+" WHERE "+spec.pk()+"=?")){
            p.setInt(1,selectedId);p.executeUpdate();reload();
        }catch(Exception ex){Theme.error(this,ex);}
    }
}
