package vn.edu.tdtu.edocument.service;

import vn.edu.tdtu.edocument.model.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DocumentProcessor {
    private final Map<String, ContentExtractionStrategy> extractionStrategies;
    private final AiOcrService aiOcrService;

    public DocumentProcessor() {
        this.aiOcrService = new MockAiOcrService();
        this.extractionStrategies = new HashMap<>();
        registerStrategy("txt", new TxtExtractionStrategy());
        registerStrategy("pdf", new OcrExtractionStrategy());
        registerStrategy("jpg", new OcrExtractionStrategy());
        registerStrategy("png", new OcrExtractionStrategy());
    }

    private void registerStrategy(String extension, ContentExtractionStrategy strategy) {
        extractionStrategies.put(extension.toLowerCase(Locale.ROOT), strategy);
    }

    private String normalizeExtension(String extension) {
        if (extension == null) {
            return "";
        }
        return extension.trim().toLowerCase(Locale.ROOT);
    }

    public void saveDraft(Document doc) {
        doc.status = "LUU_NHAP";
        System.out.println("\n=======================================================");
        System.out.println("[LUU NHAP] Dang luu ho so ID: " + doc.id + " tai buoc " + doc.currentStep);
        saveToStorage(doc);
        System.out.println("[LUU NHAP] Ho so da duoc luu.");
    }

    public void process(Document doc) {
        System.out.println("\n=======================================================");
        System.out.println("BẮT ĐẦU XỬ LÝ HỒ SƠ ID: " + doc.id);

        if (doc.id == null || doc.id.isEmpty() ||
            doc.applicantName == null || doc.applicantName.isEmpty() ||
            doc.applicantEmail == null || doc.applicantEmail.isEmpty() ||
            doc.applicantPhone == null || doc.applicantPhone.isEmpty() ||
            doc.officerName == null || doc.officerName.isEmpty() ||
            doc.officerEmail == null || doc.officerEmail.isEmpty() ||
            doc.officerPhone == null || doc.officerPhone.isEmpty() ||
            doc.documentType == null || doc.documentType.isEmpty() ||
            doc.filePath == null || doc.filePath.isEmpty() ||
            doc.fileExtension == null || doc.fileExtension.isEmpty() ||
            doc.digitalSignature == null || doc.digitalSignature.isEmpty()) {
            
            System.out.println("[LỖI TIẾP NHẬN] Thiếu trường thông tin bắt buộc. Hủy tạo hồ sơ.");
            return;
        }

        doc.status = "DA_TIEP_NHAN";
        sendNotifications(doc);

        System.out.println("[KIỂM DUYỆT] Đang kiểm tra dung lượng và định dạng...");
        if (doc.fileSizeKB > 5120) {
            System.out.println("[TỪ CHỐI] Dung lượng file " + doc.fileSizeKB + "KB vượt quá 5MB.");
            doc.status = "TU_CHOI";
            sendNotifications(doc);
            return;
        }

        String normalizedExtension = normalizeExtension(doc.fileExtension);
        ContentExtractionStrategy extractionStrategy = extractionStrategies.get(normalizedExtension);
        if (extractionStrategy == null) {
            System.out.println("[TỪ CHỐI] Định dạng " + doc.fileExtension + " không được hỗ trợ.");
            doc.status = "TU_CHOI";
            sendNotifications(doc);
            return;
        }

        System.out.println("[TRÍCH XUẤT] Đang đọc nội dung tệp đính kèm...");
        try {
            doc.extractedContent = extractionStrategy.extractText(doc.filePath, normalizedExtension);
        } catch (IOException e) {
            System.out.println("[LỖI] Không thể đọc nội dung file: " + e.getMessage());
            doc.status = "TU_CHOI";
            sendNotifications(doc);
            return;
        }

        System.out.println("[HOÀN TẤT] Hồ sơ hợp lệ và đã được lưu trữ thành công.");
        doc.status = "DANG_XET_DUYET";
        doc.currentStep = 3;
        System.out.println("[LƯU TRỮ] Đang sao chép file và xuất dữ liệu JSON...");
        saveToStorage(doc);
        sendNotifications(doc);
    }

    private void saveToStorage(Document doc) {
        String storageDirPath = "server_storage";
        File storageDir = new File(storageDirPath);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        try {
            String storedFilePath = "";
            if (doc.filePath != null && !doc.filePath.trim().isEmpty()) {
                Path sourcePath = Paths.get(doc.filePath);
                Path targetPath = Paths.get(storageDirPath + File.separator + doc.id + "_" + sourcePath.getFileName().toString());
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                storedFilePath = targetPath.toString().replace("\\", "\\\\");
            }

            String json = "{\n" +
                    "  \"id\": \"" + doc.id + "\",\n" +
                    "  \"applicantName\": \"" + doc.applicantName + "\",\n" +
                    "  \"applicantEmail\": \"" + doc.applicantEmail + "\",\n" +
                    "  \"applicantPhone\": \"" + doc.applicantPhone + "\",\n" +
                    "  \"officerName\": \"" + doc.officerName + "\",\n" +
                    "  \"officerEmail\": \"" + doc.officerEmail + "\",\n" +
                    "  \"officerPhone\": \"" + doc.officerPhone + "\",\n" +
                    "  \"documentType\": \"" + doc.documentType + "\",\n" +
                    "  \"filePath\": \"" + storedFilePath + "\",\n" +
                    "  \"fileExtension\": \"" + doc.fileExtension + "\",\n" +
                    "  \"fileSizeKB\": " + doc.fileSizeKB + ",\n" +
                    "  \"digitalSignature\": \"" + doc.digitalSignature + "\",\n" +
                    "  \"currentStep\": " + doc.currentStep + ",\n" +
                    "  \"status\": \"" + doc.status + "\"\n" +
                    "}";

            File dataFile = new File(storageDirPath + File.separator + doc.id + "_data.json");
            FileWriter writer = new FileWriter(dataFile);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            System.out.println("[LỖI HỆ THỐNG] Lỗi khi lưu trữ vật lý: " + e.getMessage());
        }
    }

    private void sendNotifications(Document doc) {
        System.out.println("  [GỬI EMAIL] -> Người nộp (" + doc.applicantEmail + "): Hồ sơ chuyển sang trạng thái " + doc.status);
        System.out.println("  [GỬI SMS]   -> Người nộp (" + doc.applicantPhone + "): Hồ sơ chuyển sang trạng thái " + doc.status);
        System.out.println("  [GỬI EMAIL] -> Cán bộ xử lý (" + doc.officerEmail + "): Hồ sơ chuyển sang trạng thái " + doc.status);
        System.out.println("  [GỬI SMS]   -> Cán bộ xử lý (" + doc.officerPhone + "): Hồ sơ chuyển sang trạng thái " + doc.status);
    }

    private interface ContentExtractionStrategy {
        String extractText(String filePath, String extension) throws IOException;
    }

    private static class TxtExtractionStrategy implements ContentExtractionStrategy {
        @Override
        public String extractText(String filePath, String extension) throws IOException {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        }
    }

    private class OcrExtractionStrategy implements ContentExtractionStrategy {
        @Override
        public String extractText(String filePath, String extension) throws IOException {
            return aiOcrService.extract(filePath, extension);
        }
    }

    private interface AiOcrService {
        String extract(String filePath, String extension) throws IOException;
    }

    private static class MockAiOcrService implements AiOcrService {
        @Override
        public String extract(String filePath, String extension) throws IOException {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new IOException("File khong ton tai: " + filePath);
            }

            return "[AI_OCR:" + extension.toUpperCase(Locale.ROOT) + "] NOI_DUNG_TRICH_XUAT_TU_" + path.getFileName().toString();
        }
    }
}