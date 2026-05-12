# Tài liệu nâng cấp Design Pattern

---

## Yêu cầu 1: Cho phép nhập hồ sơ theo từng bước, lưu nháp, nộp hồ sơ

### Phần thực hiện
- Tách luồng nhập hồ sơ thành 3 bước độc lập: thông tin cá nhân → tệp đính kèm → xác nhận.
- Hỗ trợ chuyển bước tiến/lùi, lưu nháp tại bất kỳ bước nào và tiếp tục sau.
- Dữ liệu hồ sơ được tích lũy dần qua từng bước, không yêu cầu nhập toàn bộ 13 trường một lúc.

### Pattern sử dụng

**State Pattern**
- `DocumentWorkflowState` – interface chung cho từng bước
- `Step1PersonalInfoState`, `Step2AttachmentState`, `Step3ConfirmationState` – mỗi bước tự validate và tự quyết định chuyển bước tiếp theo
- `DocumentWorkflowContext` – giữ trạng thái hiện tại, điều phối các bước

**Builder Pattern**
- `Document.Builder` – tích lũy dữ liệu dần qua từng bước, chỉ `build()` khi thực sự hoàn tất

### Lý do chọn
- **Tại sao State thay vì if/else?** Nếu dùng if/else, mỗi lần thêm bước phải sửa lại toàn bộ luồng điều hướng. State Pattern cho mỗi bước tự quyết định bước tiếp theo — thêm bước mới chỉ cần tạo class mới, không đụng code cũ (OCP).
- **Tại sao Builder thay vì constructor 13 tham số?** Người dùng điền dữ liệu theo từng bước, không thể tạo `Document` hoàn chỉnh ngay từ đầu. Builder cho phép lắp ghép từng phần và chỉ `build()` khi đủ dữ liệu.
- **Tại sao không dùng Template Method?** Template Method định nghĩa khung cố định — không phù hợp vì người dùng có thể quay lại bước trước, thứ tự không hoàn toàn tuyến tính.

---

## Yêu cầu 2: Tích hợp đọc nhiều định dạng tài liệu (TXT, PDF, JPG, PNG)

### Phần thực hiện
- Hệ thống tự nhận diện định dạng qua phần mở rộng tệp (`fileExtension`).
- TXT: đọc trực tiếp nội dung văn bản.
- PDF, JPG, PNG: đi qua lớp AI OCR (mock service mô phỏng).
- Thêm định dạng mới không cần sửa `DocumentProcessor`.

### Pattern sử dụng

**Strategy Pattern**
- `ContentExtractionStrategy` – interface chung cho thuật toán trích xuất
- `TxtExtractionStrategy` – đọc file text trực tiếp
- `OcrExtractionStrategy` – gọi AI OCR service
- `DocumentProcessor` – chọn strategy phù hợp theo `fileExtension` lúc runtime

### Lý do chọn
- **Tại sao Strategy thay vì if/else theo định dạng?** Với if/else, mỗi định dạng mới là một lần sửa `DocumentProcessor` — vi phạm OCP. Strategy tách hành vi xử lý ra ngoài, thêm định dạng chỉ cần tạo class mới và đăng ký một dòng.
- **Tại sao không dùng Factory?** Factory giải quyết bài toán *tạo đối tượng*, còn ở đây bài toán là *thay thế toàn bộ thuật toán trích xuất tại runtime* — đây đúng là vai trò của Strategy.
- **Tại sao không dùng Template Method?** Template Method phù hợp khi các bước của thuật toán có cùng khung, chỉ khác một vài bước con. Ở đây TXT và OCR hoàn toàn khác nhau về cách xử lý, không có khung chung.

---

## Yêu cầu 3: Xây dựng quy trình kiểm duyệt linh hoạt

### Phần thực hiện
- Quy trình kiểm duyệt gồm 3 trạm độc lập chạy tuần tự:
  1. Kiểm tra hợp lệ cơ bản (dung lượng ≤ 5MB, định dạng hỗ trợ).
  2. Quét an toàn bảo mật (antivirus mock).
  3. Kiểm tra chống nộp trùng lặp (SHA-256 fingerprint).
- Nếu hồ sơ không đạt bất kỳ trạm nào, quy trình dừng ngay, trả về lỗi rõ ràng và gán `TU_CHOI`.
- Thứ tự trạm và số lượng trạm có thể thay đổi linh hoạt tại runtime.

### Pattern sử dụng

**Chain of Responsibility (Pipeline)**
- `ReviewStation` – interface cho từng trạm kiểm duyệt
- `ReviewPipeline` – điều phối danh sách trạm, chạy tuần tự
- `ReviewException` – ném ra khi một trạm phát hiện lỗi, ngắt toàn bộ pipeline
- `BasicValidityReviewStation`, `AntivirusReviewStation`, `DuplicateSubmissionReviewStation` – các trạm cụ thể

### Lý do chọn
- **Tại sao Chain of Responsibility thay vì Observer?** Observer phát sự kiện đến tất cả subscriber — không có cơ chế dừng giữa chừng. Chain of Responsibility tự nhiên hơn vì mỗi trạm có thể chặn và không chuyển tiếp.
- **Tại sao không dùng Strategy?** Strategy thay thế *toàn bộ* thuật toán. Ở đây cần *nhiều bước độc lập nối tiếp nhau*, mỗi bước tự quyết định có chuyển tiếp không — đây là Chain of Responsibility.
- **Tại sao `ReviewException` thay vì return boolean?** Return boolean buộc caller phải kiểm tra kết quả sau mỗi trạm — dễ bỏ sót. Exception ngắt luồng ngay lập tức, thông tin lỗi được truyền đầy đủ lên trên mà không cần kiểm tra thủ công.

---

## Yêu cầu 4: Đăng ký nhận thông báo theo nhu cầu

### Phần thực hiện
- Mỗi tài khoản (người nộp, cán bộ tiếp nhận) tự chọn kênh nhận thông báo: `EMAIL`, `SMS`, `APP_PUSH` hoặc tổ hợp.
- Khi trạng thái hồ sơ thay đổi, hệ thống đọc cấu hình đã đăng ký và gửi đúng kênh — không gửi đồng loạt cứng như v1.0.
- Cán bộ có mức ưu tiên ≥ 2 tự động nhận thêm email cảnh báo.

### Pattern sử dụng

**Observer Pattern**
- `NotificationSubscriber` – subscriber nhận sự kiện
- `AccountNotificationSubscriber` – implementation cụ thể cho một tài khoản
- `NotificationPreferenceRegistry` – quản lý và khôi phục danh sách subscriber theo hồ sơ
- `DocumentNotificationService` – publisher, phát sự kiện thay đổi trạng thái đến tất cả subscriber

**Strategy Pattern**
- `NotificationSender` – interface cho cách gửi thông báo
- `EmailNotificationSender`, `SmsNotificationSender`, `AppPushNotificationSender` – các strategy cụ thể

### Lý do chọn
- **Tại sao kết hợp Observer + Strategy?** Hai pattern giải quyết hai bài toán khác nhau: Observer giải quyết *ai cần nhận và khi nào*; Strategy giải quyết *gửi bằng cách nào*. Tách biệt giúp thêm subscriber mới không ảnh hưởng cơ chế gửi và ngược lại.
- **Tại sao không dùng chỉ Observer?** Nếu nhúng logic gửi Email/SMS/Push thẳng vào Observer, mỗi lần thêm kênh mới phải sửa Observer — vi phạm OCP. Strategy tách logic gửi ra ngoài.
- **Tại sao không dùng Event Bus?** Event Bus phù hợp với hệ thống lớn, phân tán. Ở đây phạm vi nhỏ, Observer trực tiếp đủ rõ ràng và dễ trình bày.

---

## Yêu cầu 5: Mở rộng khả năng lưu trữ dữ liệu

### Phần thực hiện
- Tách hoàn toàn tầng lưu trữ ra khỏi `DocumentProcessor`.
- `DocumentProcessor` chỉ gọi `documentStorage.save()` và `documentStorage.loadAll()` — không biết đang dùng backend nào.
- Sử dụng **mock adapter** mô phỏng MySQL: lưu in-memory, seed sẵn dữ liệu mẫu khi khởi động, log ra màn hình để kiểm chứng (không kết nối database thật theo hướng dẫn của giảng viên).

### Pattern sử dụng

**Factory Pattern**
- `DocumentStorageFactory.create()` – tạo và trả về adapter lưu trữ, che giấu chi tiết khởi tạo

**Strategy Pattern**
- `DocumentStorage` – interface duy nhất mà nghiệp vụ nhìn thấy
- `DatabaseDocumentStorage` – mock adapter in-memory, log `[DATABASE]`

### Lý do chọn
- **Tại sao Factory thay vì `new DatabaseDocumentStorage()` trực tiếp?** Factory tập trung điểm khởi tạo adapter — sau này muốn đổi backend chỉ sửa một chỗ trong Factory, không phải tìm khắp codebase.
- **Tại sao Strategy (interface `DocumentStorage`) thay vì class cụ thể?** Dependency Inversion: nghiệp vụ phụ thuộc vào abstraction, không phụ thuộc vào implementation. Đây chính là điểm cốt lõi mà yêu cầu đề ra: *tách biệt logic xử lý nghiệp vụ và logic thao tác dữ liệu*.
- **Tại sao không kết nối database thật?** Theo ghi chú của giảng viên: *"có thể không cần kết nối Database/Cloud thật mà dùng dữ liệu giả/log ra màn hình để mô phỏng"*. Mock adapter đủ để chứng minh kiến trúc hoạt động đúng.

---

## Tổng kết

| Yêu cầu | Pattern chính | Lý do cốt lõi |
|---------|---------------|----------------|
| 1 – Nhập hồ sơ nhiều bước | **State + Builder** | Mỗi bước tự quản lý logic chuyển tiếp; dữ liệu tích lũy dần |
| 2 – Đọc nhiều định dạng | **Strategy** | Thay thế toàn bộ thuật toán trích xuất tại runtime |
| 3 – Kiểm duyệt linh hoạt | **Chain of Responsibility** | Nhiều bước độc lập nối tiếp, ngắt ngay khi thất bại |
| 4 – Thông báo theo kênh | **Observer + Strategy** | Tách *ai nhận* khỏi *cách gửi* |
| 5 – Lưu trữ linh hoạt | **Factory + Strategy** | Tách nghiệp vụ khỏi chi tiết lưu trữ qua interface |
