# 🥤 Hệ thống quản lý cửa hàng nước giải khát

> Từ phân tích nghiệp vụ và mô hình UML đến ứng dụng quản lý cửa hàng — kèm định hướng phát triển giao diện website.

Đây là đồ án phân tích, thiết kế và hiện thực một hệ thống hỗ trợ các nghiệp vụ thường gặp tại cửa hàng nước giải khát: quản lý danh mục, nhập hàng, bán hàng, giao hàng hẹn và theo dõi công nợ.

**Phạm vi mã nguồn hiện tại:** bộ sơ đồ được thiết kế bằng Draw.io và ứng dụng desktop Java Swing. Website được trình bày ở phần định hướng phát triển bên dưới; workspace hiện chưa có mã nguồn website.

---

## 📚 Mục lục

- [Tổng quan](#-tổng-quan)
- [Hành trình từ sơ đồ đến ứng dụng](#-hành-trình-từ-sơ-đồ-đến-ứng-dụng)
- [Nghiệp vụ chính](#-nghiệp-vụ-chính)
- [Định hướng phát triển website](#-định-hướng-phát-triển-website)
- [Công nghệ và kiến trúc](#-công-nghệ-và-kiến-trúc)
- [Bắt đầu sử dụng](#-bắt-đầu-sử-dụng)
- [Kiểm thử](#-kiểm-thử)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)

## ✨ Tổng quan

Ứng dụng giúp nhân viên cửa hàng theo dõi dữ liệu và xử lý các luồng công việc từ lúc đặt hàng nhà cung cấp đến khi bán hàng, giao hàng và thu nợ.

| Thông tin | Chi tiết |
|---|---|
| Loại ứng dụng hiện tại | Ứng dụng desktop |
| Ngôn ngữ | Java 17 trở lên |
| Giao diện | Java Swing |
| Thiết kế sơ đồ | Draw.io |
| Lưu trữ | Tệp dữ liệu cục bộ, không cần cài cơ sở dữ liệu |
| Đơn vị tiền tệ | Đồng Việt Nam (VND) |

### Các phân hệ

- **Tổng quan và thống kê:** xem nhanh tình hình kinh doanh.
- **Danh mục:** nhãn hiệu, nhà cung cấp, nước giải khát và khách hàng.
- **Nhập hàng:** lập đơn đặt hàng, ghi nhận các đợt giao và cập nhật tồn kho.
- **Bán hàng:** lập hóa đơn, giao phần hàng có sẵn và theo dõi phần còn thiếu.
- **Giao hàng hẹn:** quản lý lịch giao, giao nhiều đợt hoặc tách phiếu hẹn.
- **Công nợ:** ghi nợ khách quen, theo dõi hạn trả và thu tiền một phần hoặc toàn bộ.
- **Lưu dữ liệu:** tự lưu thay đổi vào `du_lieu/cua_hang.dat`.

## 🧭 Hành trình từ sơ đồ đến ứng dụng

Sáu sơ đồ trong [`SoDo_DrawIO/`](./SoDo_DrawIO/) mô tả các góc nhìn khác nhau của bài toán, từ vai trò người dùng đến luồng nghiệp vụ cụ thể:

| Sơ đồ | Nội dung |
|---|---|
| [Câu 1 — Use Case tổng quát](./SoDo_DrawIO/Cau_1_UseCase_TongQuat.drawio) | Xác định tác nhân và các chức năng chính của hệ thống. |
| [Câu 2 — Lớp, thuộc tính và phương thức](./SoDo_DrawIO/Cau_2_CacLop_ThuocTinh_PhuongThuc.drawio) | Mô hình hóa các đối tượng nghiệp vụ và mối quan hệ dữ liệu. |
| [Câu 3 — Class nhập hàng](./SoDo_DrawIO/Cau_3_Class_NhapHang.drawio) | Đi sâu vào cấu trúc lớp phục vụ nghiệp vụ nhập hàng. |
| [Câu 4 — Use Case phân rã](./SoDo_DrawIO/Cau_4_UseCase_PhanRa.drawio) | Phân rã các chức năng để làm rõ phạm vi xử lý. |
| [Câu 5 — Activity bán hàng](./SoDo_DrawIO/Cau_5_Activity_BanHang.drawio) | Mô tả trình tự và các nhánh xử lý khi bán hàng. |
| [Câu 6 — Sequence ghi nợ](./SoDo_DrawIO/Cau_6_Sequence_GhiNo.drawio) | Thể hiện tương tác giữa các thành phần khi lập và ghi nhận công nợ. |

Các mô hình này là cơ sở để tổ chức chương trình thành ba phần: **mô hình dữ liệu**, **xử lý nghiệp vụ** và **giao diện người dùng**. Các quy tắc quan trọng như giới hạn số đợt giao, kiểm tra tồn kho và điều kiện ghi nợ được đặt trong lớp dịch vụ nghiệp vụ để có thể kiểm thử độc lập với giao diện.

## 🧾 Nghiệp vụ chính

### Quản lý danh mục và tồn kho

Lưu thông tin nhãn hiệu, nhà cung cấp, sản phẩm và khách hàng; mỗi sản phẩm có giá nhập, giá bán, đơn vị tính và số lượng tồn kho. Dữ liệu ban đầu có sẵn để thuận tiện trải nghiệm ứng dụng.

### Nhập hàng

Nhân viên tạo đơn với nhà cung cấp và mặt hàng tương ứng, sau đó ghi nhận số lượng thực nhận. Một đơn có thể được giao thành nhiều đợt, tối đa **3 đợt**; số lượng nhận không được vượt quá số lượng đã đặt. Tồn kho tăng khi hàng được nhận.

### Bán hàng và giao theo hẹn

Khi lập hóa đơn, hệ thống kiểm tra số lượng bán và tồn kho. Nếu chưa đủ hàng, phần còn thiếu có thể được tạo thành phiếu hẹn để giao sau. Phiếu hẹn hỗ trợ giao hàng theo nhiều lần hoặc tách thành các đợt riêng.

### Công nợ

Chức năng ghi nợ dành cho khách hàng quen. Hệ thống theo dõi số tiền phải trả, số tiền đã thu và dư nợ; hỗ trợ thu tiền từng phần, đồng thời không cho phép thu vượt số dư còn nợ.

### Lưu trữ cục bộ

Ứng dụng tự nạp dữ liệu khi khởi động và lưu các thay đổi vào tệp `du_lieu/cua_hang.dat`. Nếu chưa có dữ liệu lưu, ứng dụng khởi tạo bộ dữ liệu mẫu.

## 🌐 Định hướng phát triển website

Các sơ đồ và nghiệp vụ hiện có có thể được sử dụng làm nền tảng để phát triển phiên bản website. Đây là **định hướng chức năng**, không phải mô tả một website đã được triển khai trong workspace.

### Giao diện dự kiến

- Trang **Tổng quan** với doanh thu, tồn kho và công nợ cần theo dõi.
- Các trang quản lý **sản phẩm, nhãn hiệu, nhà cung cấp và khách hàng**.
- Luồng **đặt hàng – nhận hàng** có theo dõi trạng thái và số lượng từng đợt.
- Luồng **lập hóa đơn – giao hàng hẹn** có cập nhật tiến độ giao.
- Trang **công nợ** để xem hạn trả và ghi nhận thanh toán.
- Trang **thống kê** hỗ trợ cửa hàng theo dõi hoạt động kinh doanh.

### Các bước triển khai tiếp theo

1. Chọn framework giao diện và công nghệ phía máy chủ phù hợp yêu cầu môn học.
2. Thiết kế API và cơ sở dữ liệu dựa trên các lớp nghiệp vụ trong sơ đồ.
3. Chuyển các quy tắc nghiệp vụ hiện có thành dịch vụ phía máy chủ và kiểm thử chúng.
4. Xây dựng giao diện web tương ứng với các phân hệ của ứng dụng.
5. Bổ sung đăng nhập, phân quyền và kiểm thử tích hợp trước khi triển khai.

## 🏗️ Công nghệ và kiến trúc

Ứng dụng Java hiện tại được tổ chức theo hướng tách biệt trách nhiệm:

- [`Models.java`](./SanPham_JavaSwing/src/Models.java): các mô hình dữ liệu như sản phẩm, hóa đơn, đơn nhập hàng, phiếu hẹn và phiếu ghi nợ.
- [`BusinessService.java`](./SanPham_JavaSwing/src/BusinessService.java): quy tắc nghiệp vụ và kiểm tra điều kiện xử lý.
- [`DataStore.java`](./SanPham_JavaSwing/src/DataStore.java): đọc và lưu dữ liệu cục bộ.
- [`Main.java`](./SanPham_JavaSwing/src/Main.java): giao diện Java Swing và các thao tác tương tác với người dùng.
- [`BusinessTests.java`](./SanPham_JavaSwing/src/BusinessTests.java): kiểm thử các quy tắc nghiệp vụ.

## 🚀 Bắt đầu sử dụng

### Yêu cầu

- JDK **17 trở lên** nếu chạy hoặc biên dịch mã nguồn.
- Môi trường có giao diện đồ họa để mở ứng dụng Java Swing.

### Windows — chạy ứng dụng đóng gói

1. Cài Java 17 trở lên.
2. Mở thư mục [`SanPham_JavaSwing/`](./SanPham_JavaSwing/).
3. Chạy `mo_phan_mem.bat`.

Tệp khởi chạy này mở ứng dụng từ `CuaHangNuocGiaiKhat.jar`.

### Windows — biên dịch từ mã nguồn

Mở PowerShell trong thư mục `SanPham_JavaSwing`, sau đó chạy:

```powershell
New-Item -ItemType Directory -Force bin
javac -encoding UTF-8 -d bin src\Models.java src\BusinessService.java src\DataStore.java src\Main.java src\BusinessTests.java
java -cp bin ngk.Main
```

### Linux/macOS — chạy ứng dụng đóng gói

Trong thư mục `SanPham_JavaSwing`, chạy:

```bash
chmod +x mo_phan_mem.sh
./mo_phan_mem.sh
```

## ✅ Kiểm thử

Trên Windows, mở PowerShell trong `SanPham_JavaSwing` và chạy:

```powershell
.\kiem_tra_chuc_nang.bat
```

Bộ kiểm thử bao phủ dữ liệu mẫu, nhập hàng nhiều đợt, kiểm tra tồn kho, bán hàng, giao hàng hẹn, thu nợ một phần/toàn bộ và lưu/khôi phục dữ liệu.

## 📁 Cấu trúc dự án

```text
Lab8/
├── README.md
├── SoDo_DrawIO/
│   ├── Cau_1_UseCase_TongQuat.drawio
│   ├── Cau_2_CacLop_ThuocTinh_PhuongThuc.drawio
│   ├── Cau_3_Class_NhapHang.drawio
│   ├── Cau_4_UseCase_PhanRa.drawio
│   ├── Cau_5_Activity_BanHang.drawio
│   └── Cau_6_Sequence_GhiNo.drawio
└── SanPham_JavaSwing/
    ├── src/
    ├── bin/
    ├── CuaHangNuocGiaiKhat.jar
    ├── mo_phan_mem.bat
    ├── mo_phan_mem.sh
    └── kiem_tra_chuc_nang.bat
```

---

<p align="center">
  Được xây dựng từ phân tích nghiệp vụ, mô hình hóa UML và hiện thực hóa thành ứng dụng quản lý cửa hàng.
</p>
