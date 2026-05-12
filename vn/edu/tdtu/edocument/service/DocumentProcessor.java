package vn.edu.tdtu.edocument.service;

import vn.edu.tdtu.edocument.model.Document;
import vn.edu.tdtu.edocument.service.extraction.ContentExtractionStrategy;
import vn.edu.tdtu.edocument.service.extraction.OcrExtractionStrategy;
import vn.edu.tdtu.edocument.service.extraction.TxtExtractionStrategy;
import vn.edu.tdtu.edocument.service.notification.DocumentNotificationService;
import vn.edu.tdtu.edocument.service.ocr.AiOcrService;
import vn.edu.tdtu.edocument.service.ocr.MockAiOcrService;
import vn.edu.tdtu.edocument.service.review.AntivirusReviewStation;
import vn.edu.tdtu.edocument.service.review.BasicValidityReviewStation;
import vn.edu.tdtu.edocument.service.review.DuplicateSubmissionReviewStation;
import vn.edu.tdtu.edocument.service.review.ReviewException;
import vn.edu.tdtu.edocument.service.review.ReviewPipeline;
import vn.edu.tdtu.edocument.service.review.ReviewStation;
import vn.edu.tdtu.edocument.service.storage.DocumentStorage;
import vn.edu.tdtu.edocument.service.storage.DocumentStorageFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DocumentProcessor {
    private final Map<String, ContentExtractionStrategy> extractionStrategies;
    private final ReviewPipeline reviewPipeline;
    private final DuplicateSubmissionReviewStation duplicateSubmissionReviewStation;
    private final DocumentNotificationService notificationService;
    private DocumentStorage documentStorage;

    public DocumentProcessor() {
        AiOcrService aiOcrService = new MockAiOcrService();
        this.extractionStrategies = new HashMap<>();
        this.reviewPipeline = new ReviewPipeline();
        this.notificationService = new DocumentNotificationService();
        this.documentStorage = DocumentStorageFactory.create();
        registerStrategy("txt", new TxtExtractionStrategy());
        registerStrategy("pdf", new OcrExtractionStrategy(aiOcrService));
        registerStrategy("jpg", new OcrExtractionStrategy(aiOcrService));
        registerStrategy("png", new OcrExtractionStrategy(aiOcrService));
        this.duplicateSubmissionReviewStation = new DuplicateSubmissionReviewStation();
        configureDefaultReviewStations();
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
            
            System.out.println("[LOI TIEP NHAN] Thieu truong thong tin bat buoc. Huy tao ho so.");
            return;
        }

        doc.status = "DA_TIEP_NHAN";
        sendNotifications(doc);

        String normalizedExtension = normalizeExtension(doc.fileExtension);
        try {
            reviewPipeline.execute(doc);
        } catch (ReviewException e) {
            System.out.println("[TU CHOI] " + e.getMessage());
            doc.status = "TU_CHOI";
            sendNotifications(doc);
            return;
        }

        ContentExtractionStrategy extractionStrategy = extractionStrategies.get(normalizedExtension);
        if (extractionStrategy == null) {
            System.out.println("[TU CHOI] Khong tim thay bo xu ly cho dinh dang " + doc.fileExtension + ".");
            doc.status = "TU_CHOI";
            sendNotifications(doc);
            return;
        }

        System.out.println("[TRICH XUAT] Dang doc noi dung tep dinh kem...");
        try {
            doc.extractedContent = extractionStrategy.extractText(doc.filePath, normalizedExtension);
        } catch (IOException e) {
            System.out.println("[LOI] Khong the doc noi dung file: " + e.getMessage());
            doc.status = "TU_CHOI";
            sendNotifications(doc);
            return;
        }

        doc.status = "DANG_XET_DUYET";
        doc.currentStep = 3;
        System.out.println("[HOAN TAT] Ho so hop le, dang luu tru...");
        saveDocument(doc);
        duplicateSubmissionReviewStation.registerProcessedDocument(doc);
        sendNotifications(doc);
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
        saveDocument(doc);
        System.out.println("[LUU NHAP] Ho so da duoc luu.");
    }

    private void sendNotifications(Document doc) {
        notificationService.notifyStatusChanged(doc);
    }

    private void configureDefaultReviewStations() {
        reviewPipeline.setStations(Arrays.asList(
                new BasicValidityReviewStation(extractionStrategies.keySet()),
                new AntivirusReviewStation(),
                duplicateSubmissionReviewStation
        ));
    }

    public void setReviewStations(List<ReviewStation> stations) {
        reviewPipeline.setStations(stations);
    }

    public void addReviewStation(ReviewStation station) {
        reviewPipeline.addStation(station);
    }

    public void clearReviewStations() {
        reviewPipeline.clearStations();
    }

    public List<Document> loadDocuments() {
        try {
            return documentStorage.loadAll();
        } catch (IOException e) {
            System.out.println("[LOI HE THONG] Khong the tai du lieu: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private void saveDocument(Document doc) {
        try {
            documentStorage.save(doc);
        } catch (IOException e) {
            System.out.println("[LOI HE THONG] Loi khi luu tru du lieu: " + e.getMessage());
        }
    }
}