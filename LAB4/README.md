# Lab 4 – Hệ thống e-SHOPPING

## Giới thiệu

Bài lab xây dựng bản mẫu hệ thống mua sắm trực tuyến **e-SHOPPING**. Nội dung hiện có gồm giao diện desktop bằng Java Swing, thiết kế cơ sở dữ liệu SQL Server và các sơ đồ phân tích/thiết kế UML.

## Nội dung đã thực hiện

### Giao diện ứng dụng Java Swing

Mã nguồn nằm trong thư mục `e-shopping/src/`. Ứng dụng có giao diện tiếng Việt và các màn hình:

- **Trang chủ:** giới thiệu hệ thống, điều hướng đến sản phẩm, nhóm sản phẩm và giỏ hàng; hiển thị một số sản phẩm nổi bật.
- **Sản phẩm và nhóm sản phẩm:** hiển thị danh sách mẫu, xem thông tin chi tiết và thao tác thêm sản phẩm vào giỏ ở mức giao diện.
- **Giỏ hàng:** hiển thị sản phẩm, số lượng và tổng tiền mẫu; có nút cập nhật số lượng, xóa sản phẩm và đặt hàng.
- **Tài khoản:** màn hình đăng nhập và đăng ký, có kiểm tra các trường bắt buộc.
- **Đặt hàng và thanh toán:** nhập thông tin người nhận, chọn hình thức/khu vực giao hàng và nhập thông tin thẻ.
- **Xác nhận đơn hàng:** hiển thị thông tin đơn hàng mẫu sau thao tác thanh toán.

> Các màn hình hiện sử dụng dữ liệu minh họa viết sẵn để trình diễn luồng chức năng. Mã nguồn giao diện chưa thực hiện kết nối cơ sở dữ liệu hay xử lý giao dịch thanh toán thực tế.

### Cơ sở dữ liệu SQL Server

Script `SQLQuery1.sql` tạo database `ESHOPPING` và định nghĩa 15 bảng:

1. `NHOMSANPHAM`
2. `NHASANXUAT`
3. `SANPHAM`
4. `KHACHHANG`
5. `GIOHANG`
6. `CT_GIOHANG`
7. `HINHTHUCGIAOHANG`
8. `KHUVUCGIAOHANG`
9. `CHIPHIGIAOHANG`
10. `DONDATHANG`
11. `CT_DONDATHANG`
12. `THETINDUNG`
13. `THANHTOAN`
14. `PHITHANHTOAN`
15. `EMAILXACNHAN`

Thiết kế có khóa chính, khóa ngoại, các ràng buộc `UNIQUE` và `CHECK`, giá trị mặc định và cột tính thành tiền cho chi tiết đơn hàng.

### Sơ đồ phân tích và thiết kế

- `e-shopping/uml/`: sơ đồ Use Case, Class Analysis và State.
- `e-shopping/LyThuyetTuan5/`: các sơ đồ Class, ERD, Sequence, State và Use Case.

## Cấu trúc thư mục

```text
.
├── README.md
├── SQLQuery1.sql
└── e-shopping/
    ├── LyThuyetTuan5/   # Các sơ đồ thiết kế
    ├── src/             # Mã nguồn Java Swing và các file .class
    └── uml/             # Sơ đồ Use Case, Class Analysis, State
```

## Chạy ứng dụng

Cần cài JDK. Mở terminal tại thư mục `e-shopping/src/`, sau đó biên dịch và chạy:

```bash
javac *.java
java GiaoDienChinh
```

## Tạo cơ sở dữ liệu

Mở `SQLQuery1.sql` bằng SQL Server Management Studio (hoặc công cụ tương thích), kết nối SQL Server rồi thực thi script để tạo database và các bảng.
