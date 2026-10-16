# Hệ thống quản lý hồ sơ bệnh án

Bài tập gồm bộ 6 sơ đồ UML được vẽ bằng Draw.io và ứng dụng desktop quản lý hồ sơ bệnh án, viết bằng Java Swing, lưu dữ liệu trên Microsoft SQL Server.

> Đây là sản phẩm minh họa phục vụ học tập. Chỉ dùng dữ liệu giả lập; không nhập thông tin sức khỏe hoặc thông tin định danh của người thật.

## 1. Thành phần dự án

```text
.
├── 01_Xac_dinh_lop.drawio
├── 02_Class_Diagram.drawio
├── 03_Use_Case_Tong_Quat.drawio
├── 04_Use_Case_Chi_Tiet.drawio
├── 05_Activity_Diagram.drawio
├── 06_Sequence_Diagram.drawio
└── QuanLyHoSoBenhAn_Java_SQLServer/
    ├── database/
    ├── src/main/java/vn/edu/benhan/
    ├── tests/
    ├── config.properties.example
    ├── pom.xml
    └── CHAY_WINDOWS.bat
```

## 2. Quy trình xây dựng bộ sơ đồ

Thứ tự dưới đây đi từ xác định đối tượng, mô tả nghiệp vụ đến mô tả tương tác chi tiết. Các tệp `.drawio` là sơ đồ nguồn có thể mở lại và chỉnh sửa.

1. **Xác định lớp** — mở [`01_Xac_dinh_lop.drawio`](./01_Xac_dinh_lop.drawio), liệt kê các lớp chính của bài toán, mã định danh, thuộc tính và thao tác. Ở bước này tập trung xác định lớp, chưa thể hiện quan hệ giữa các lớp.
2. **Sơ đồ lớp** — mở [`02_Class_Diagram.drawio`](./02_Class_Diagram.drawio), bổ sung quan hệ và bội số giữa các lớp đã xác định, chẳng hạn bệnh nhân, đăng ký, hồ sơ, lịch hẹn, bác sĩ, khoa, phiếu khám, xét nghiệm và toa thuốc. Đối chiếu tên/mã lớp với các bảng trong CSDL.
3. **Use Case tổng quát** — mở [`03_Use_Case_Tong_Quat.drawio`](./03_Use_Case_Tong_Quat.drawio), xác định ranh giới hệ thống, các tác nhân (bệnh nhân, nhân viên tiếp nhận/CSKH, bác sĩ, nhân viên xét nghiệm) và chức năng chính của từng tác nhân.
4. **Use Case chi tiết** — mở [`04_Use_Case_Chi_Tiet.drawio`](./04_Use_Case_Chi_Tiet.drawio), chọn quy trình khám bệnh để phân rã các tương tác: tiếp nhận đăng ký, tạo/tra cứu hồ sơ, khám, chỉ định và trả kết quả xét nghiệm, lập toa và hướng dẫn sử dụng thuốc. Dùng quan hệ `include` cho các hành động bắt buộc được tái sử dụng.
5. **Activity Diagram** — mở [`05_Activity_Diagram.drawio`](./05_Activity_Diagram.drawio), diễn tả thứ tự xử lý của quy trình khám, các điểm quyết định (đã có hồ sơ, có cần xét nghiệm, có kê thuốc) và các nhánh Có/Không.
6. **Sequence Diagram** — mở [`06_Sequence_Diagram.drawio`](./06_Sequence_Diagram.drawio), mô tả theo thời gian quy trình bác sĩ lập toa: hệ thống tra cứu hồ sơ/lịch sử khám, bác sĩ nhập thuốc và liều dùng, hệ thống kiểm tra rồi lưu toa, sau đó trả thông tin để giao toa cho bệnh nhân.

### Mở và chỉnh sửa sơ đồ

1. Mở [app.diagrams.net](https://app.diagrams.net/) hoặc Draw.io Desktop.
2. Chọn mở sơ đồ từ thiết bị (Open from Device) và chọn một tệp `.drawio` ở thư mục gốc.
3. Chỉnh sửa sơ đồ, lưu lại dưới định dạng `.drawio` để giữ khả năng chỉnh sửa.
4. Nếu cần nộp/xem nhanh, dùng **File → Export as** để xuất PNG, PDF hoặc SVG. Giữ tệp `.drawio` gốc cùng bản xuất.
5. Khi thay đổi quy trình hoặc dữ liệu, cập nhật các sơ đồ liên quan để thống nhất tác nhân, tên lớp, quan hệ, thứ tự nghiệp vụ và các trạng thái.

## 3. Sản phẩm thực hiện được

- Đăng nhập bằng tài khoản và phân quyền chức năng theo vai trò.
- Quản lý bệnh nhân, khoa, bác sĩ, lịch hẹn, phí năm, phiếu khám, xét nghiệm, toa thuốc và chi tiết toa.
- Đăng ký lần đầu theo luồng tạo phiếu đăng ký, thu phí năm và cấp hồ sơ bệnh án. Các thao tác này nằm trong một giao dịch; nếu có lỗi thì được hoàn tác.
- Theo dõi hồ sơ bệnh án và các lượt khám.
- Ghi nhận thanh toán viện phí, sau đó chuyển phiếu khám sang trạng thái đã lưu.
- Khám định kỳ quý được miễn phí khi bệnh nhân đã đóng phí năm hiện tại và chưa dùng lượt miễn phí trong quý đó.
- Xem tổng quan số bệnh nhân, hồ sơ, lượt khám, lịch hẹn chờ, xét nghiệm chờ và phiếu chờ thanh toán.
- Tra cứu các báo cáo hồ sơ, lịch sử khám, lệ phí năm, toa thuốc và thanh toán; xuất kết quả ra CSV.

### Vai trò demo

Ứng dụng tự tạo các tài khoản bên dưới khi bảng `TaiKhoan` còn trống. Tài khoản được tạo một lần; nếu bảng đã có tài khoản thì ứng dụng không tự thêm lại.

| Vai trò | Tên đăng nhập | Mật khẩu demo | Quyền chỉnh sửa chính |
|---|---|---|---|
| Quản trị | `admin` | `admin123` | Tất cả chức năng |
| Bác sĩ | `bacsi` | `bs123456` | Phiếu khám, xét nghiệm, toa thuốc |
| Tiếp nhận | `tiepnhan` | `tn123456` | Bệnh nhân, lịch hẹn, phí năm, đăng ký |
| Chăm sóc | `chamsoc` | `cs123456` | Thanh toán viện phí |

Các vai trò khác vẫn có thể mở nhiều màn hình để xem dữ liệu; nút thêm/sửa/xóa được bật theo quyền. Đây là phân quyền ở ứng dụng demo, không thay thế cấu hình bảo mật riêng trên SQL Server.

## 4. Công nghệ và yêu cầu

- Windows
- JDK 17 trở lên
- Apache Maven (có lệnh `mvn` trong `PATH`)
- Microsoft SQL Server đang chạy và cho phép kết nối TCP/IP
- SQL Server Management Studio (SSMS) hoặc công cụ tương đương để chạy script SQL
- Cổng SQL Server mặc định: `1433`

Ứng dụng dùng Java Swing, Maven và Microsoft JDBC Driver for SQL Server. Mã nguồn Java nằm trong `QuanLyHoSoBenhAn_Java_SQLServer/src/main/java/vn/edu/benhan/`.

## 5. Cài đặt và chạy ứng dụng trên Windows

### Bước 1: Tạo cơ sở dữ liệu

1. Mở SSMS, kết nối tới SQL Server.
2. Mở và chạy [`01_Tao_CSDL.sql`](./QuanLyHoSoBenhAn_Java_SQLServer/database/01_Tao_CSDL.sql). Script tạo CSDL `QuanLyHoSoBenhAn`, các bảng và các view thống kê.
3. Mở và chạy [`02_Du_Lieu_Mau.sql`](./QuanLyHoSoBenhAn_Java_SQLServer/database/02_Du_Lieu_Mau.sql) để thêm khoa, bác sĩ và bệnh nhân giả lập.
4. Có thể chạy [`03_Kiem_Tra_CSDL.sql`](./QuanLyHoSoBenhAn_Java_SQLServer/database/03_Kiem_Tra_CSDL.sql) để kiểm tra bảng, dữ liệu và các view.

Script dữ liệu mẫu **không** tạo phiếu đăng ký hoặc hồ sơ bệnh án mẫu. Sau khi chạy ứng dụng, tạo bệnh nhân hoặc chọn một bệnh nhân mẫu rồi thực hiện mục **Đăng ký + lập hồ sơ**.

### Bước 2: Cấu hình kết nối

Tạo bản sao [`config.properties.example`](./QuanLyHoSoBenhAn_Java_SQLServer/config.properties.example) với tên `config.properties` trong thư mục ứng dụng, sau đó cập nhật thông tin kết nối theo máy của bạn:

```properties
server=localhost
port=1433
database=QuanLyHoSoBenhAn
user=sa
password=MAT_KHAU_SQL_SERVER
encrypt=true
trustServerCertificate=true
loginTimeout=6
```

Thay `server`, `port`, `user` và `password` theo cấu hình SQL Server thực tế. Tệp `config.properties` đã được đưa vào `.gitignore`; không đưa mật khẩu hoặc thông tin kết nối riêng tư lên Git. `trustServerCertificate=true` chỉ phù hợp môi trường local/lab; môi trường triển khai cần cấu hình chứng chỉ tin cậy phù hợp.

### Bước 3: Khởi chạy

Từ thư mục `QuanLyHoSoBenhAn_Java_SQLServer`:

- Cách đơn giản: chạy `CHAY_WINDOWS.bat`.
- Hoặc mở PowerShell/CMD tại thư mục đó và chạy:

```powershell
mvn compile exec:java
```

Maven sẽ tải JDBC driver và các plugin được khai báo trong `pom.xml`, biên dịch rồi mở ứng dụng. Khi cửa sổ đăng nhập xuất hiện, dùng một tài khoản demo ở bảng trên.

### Bước 4: Thử luồng nghiệp vụ mẫu

1. Đăng nhập bằng `admin / admin123`.
2. Mở **Bệnh nhân** để tạo bệnh nhân mới, hoặc dùng bệnh nhân giả lập đã được thêm từ script SQL.
3. Mở **Đăng ký + lập hồ sơ**, tải danh sách, chọn bệnh nhân, nhập lệ phí và chọn **Lập hồ sơ và thu phí**. Hệ thống tạo phiếu đăng ký, phí năm và hồ sơ trong cùng một giao dịch.
4. Kiểm tra mã hồ sơ ở **Hồ sơ bệnh án** hoặc **Tổng quan**.
5. Tạo lịch hẹn nếu cần, rồi thêm phiếu khám tại **Phiếu khám**. Bổ sung xét nghiệm/toa thuốc theo quy trình; đổi trạng thái phiếu khám sang **Chờ thanh toán** khi khám xong.
6. Mở **Xuất viện + thanh toán**, chọn phiếu đang chờ và ghi nhận thanh toán để lưu lượt khám vào bệnh án. Với khám định kỳ quý, ứng dụng kiểm tra phí năm và giới hạn một lượt miễn phí mỗi quý.
7. Mở **Báo cáo - tra cứu** để xem các báo cáo và xuất CSV khi cần.

## 6. Cấu trúc mã nguồn ứng dụng

| Thành phần | Nhiệm vụ |
|---|---|
| `Main.java` | Điểm vào chương trình, kiểm tra kết nối, khởi tạo tài khoản demo và điều hướng giao diện |
| `Auth.java` | Đăng nhập, băm mật khẩu demo bằng PBKDF2 và kiểm tra quyền theo vai trò |
| `Db.java` | Đọc cấu hình, mở kết nối JDBC và thực hiện giao dịch |
| `Specs.java` | Khai báo metadata bảng/cột cho các màn hình dữ liệu |
| `DataPanel.java` | Màn hình danh sách, tìm kiếm và thao tác thêm/sửa/xóa theo quyền |
| `Workflows.java` | Các nghiệp vụ nhiều bước: đăng ký/lập hồ sơ và thanh toán/lưu lượt khám |
| `DashboardPanel.java` | Bảng tổng quan và danh sách hồ sơ gần đây |
| `ReportsPanel.java` | Tra cứu báo cáo và xuất CSV |
| `Theme.java` | Thành phần giao diện và thông báo dùng chung |

## 7. Lưu ý bảo mật và dữ liệu

- Đây là ứng dụng demo, không được thiết kế để lưu hồ sơ bệnh án thật hoặc thay thế hệ thống y tế đã được thẩm định.
- Tài khoản/mật khẩu mẫu được ghi trong mã nguồn và chỉ phục vụ trình diễn. Đổi hoặc xóa tài khoản demo trước khi triển khai ở môi trường dùng chung.
- Mật khẩu người dùng được lưu dạng băm có salt; mật khẩu kết nối SQL Server trong `config.properties` vẫn là bí mật cần bảo vệ.
- Báo cáo CSV có thể chứa dữ liệu sức khỏe. Chỉ xuất trên thiết bị được phép và bảo vệ tệp sau khi xuất.
- Không dùng `trustServerCertificate=true` như cấu hình mặc định cho môi trường production.
