# Hệ thống Tiếp nhận và Xử lý Hồ sơ Điện tử – v2.0

Dự án nâng cấp hệ thống quản lý hồ sơ điện tử từ phiên bản 1.0 lên 2.0, áp dụng các **Design Patterns** để đáp ứng 5 yêu cầu nghiệp vụ mới.

---

## Cấu trúc thư mục

```
panda-midtern-project/
├── README.md
├── run.bat                          ← Script chạy nhanh trên Windows
├── DESIGN_PATTERN_UPGRADE_NOTES.md  ← Tài liệu thiết kế chi tiết
└── vn/edu/tdtu/edocument/
    ├── MainSwingUI.java             ← Giao diện chính
    ├── AddDocumentDialog.java       ← Dialog nhập hồ sơ theo từng bước
    ├── model/
    │   └── Document.java           ← Model hồ sơ + Builder
    └── service/
        ├── DocumentProcessor.java   ← Bộ xử lý nghiệp vụ trung tâm
        ├── extraction/              ← Strategy trích xuất nội dung (Yc 2)
        ├── notification/            ← Observer + Strategy gửi tb (Yc 4)
        ├── ocr/                     ← Mock AI OCR service
        ├── review/                  ← Pipeline kiểm duyệt (Yc 3)
        ├── storage/                 ← Factory + Strategy lưu trữ (Yc 5)
        └── workflow/                ← State Machine luồng nộp hồ sơ (Yc 1)
```

---

## Yêu cầu hệ thống

| Thành phần | Phiên bản tối thiểu |
|------------|---------------------|
| Java JDK   | 8 trở lên           |
| Hệ điều hành | Windows / macOS / Linux |

Kiểm tra Java đã cài chưa:
```bash
java -version
javac -version
```

---

## Hướng dẫn chạy – Step by Step

### Cách 1: Dùng file `run.bat` (Windows – nhanh nhất)

**Bước 1 – Clone repository về máy:**
```cmd
git clone https://github.com/tecnaaa/panda-midtern-project.git
```

**Bước 2 – Di chuyển vào thư mục dự án:**
```cmd
cd panda-midtern-project
```

**Bước 3 – Double-click vào file `run.bat`**
- Cửa sổ cmd sẽ mở ra, tự động biên dịch và khởi chạy ứng dụng
- Giao diện Swing sẽ hiện ra sau vài giây

> Nếu gặp lỗi "javac not found", hãy đảm bảo JDK đã được thêm vào PATH của Windows.

---

### Cách 2: Chạy thủ công từ Command Prompt / Terminal

**Bước 1 – Clone repository về máy:**
```cmd
git clone https://github.com/tecnaaa/panda-midtern-project.git
```

**Bước 2 – Di chuyển vào thư mục dự án:**
```cmd
cd panda-midtern-project
```

**Bước 3 – Biên dịch toàn bộ mã nguồn:**

Trên Windows (Command Prompt):
```cmd
javac -encoding UTF-8 -d out vn\edu\tdtu\edocument\MainSwingUI.java
```

Trên macOS / Linux:
```bash
javac -encoding UTF-8 -d out vn/edu/tdtu/edocument/MainSwingUI.java
```

> Lệnh này tự động biên dịch tất cả các file `.java` phụ thuộc.

**Bước 4 – Chạy ứng dụng:**
```cmd
java -cp out vn.edu.tdtu.edocument.MainSwingUI
```

---

## Hướng dẫn sử dụng

### Nộp hồ sơ mới (Yêu cầu 1 – State Pattern)
1. Nhấn nút **"Thêm mới hồ sơ"** ở thanh công cụ
2. **Bước 1:** Nhập Tên, Email, Số điện thoại người nộp; chọn kênh thông báo mong muốn (EMAIL / SMS / APP_PUSH)
3. Nhấn **"Tiếp tục"** để sang bước 2
4. **Bước 2:** Điền thông tin cán bộ tiếp nhận, chọn loại hồ sơ, nhấn **"Chọn..."** để đính kèm tệp `.txt`, `.pdf`, `.jpg`, hoặc `.png`
5. Nhấn **"Tiếp tục"** để sang bước 3
6. **Bước 3:** Nhập chữ ký số, nhấn **"Nộp hồ sơ"**

### Lưu nháp và tiếp tục sau
- Tại bất kỳ bước nào, nhấn **"Lưu nháp"** để lưu tiến trình
- Trên màn hình chính, chọn hàng có trạng thái `LUU_NHAP`, nhấn **"Tiếp tục hồ sơ nháp"** để tiếp tục

### Xem chi tiết hồ sơ
- Click vào bất kỳ hàng nào trong bảng danh sách – chi tiết hồ sơ sẽ xuất hiện trong vùng **Log** phía dưới

---
