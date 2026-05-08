package vn.edu.tdtu.edocument.model;

import java.util.UUID;

public class Document {
    public String id;
    public String applicantName;
    public String applicantEmail;
    public String applicantPhone;
    public String officerName;
    public String officerEmail;
    public String officerPhone;
    public String documentType;
    public String filePath;
    public String fileExtension;
    public long fileSizeKB;
    public String digitalSignature;
    public String extractedContent;
    public String status;
    public int currentStep;

    public Document(String id, String applicantName, String applicantEmail, String applicantPhone,
                    String officerName, String officerEmail, String officerPhone,
                    String documentType, String filePath, String fileExtension, 
                    long fileSizeKB, String digitalSignature, String extractedContent, String status) {
        this(id, applicantName, applicantEmail, applicantPhone,
                officerName, officerEmail, officerPhone,
                documentType, filePath, fileExtension,
                fileSizeKB, digitalSignature, extractedContent, status, 3);
    }

    public Document(String id, String applicantName, String applicantEmail, String applicantPhone,
                    String officerName, String officerEmail, String officerPhone,
                    String documentType, String filePath, String fileExtension,
                    long fileSizeKB, String digitalSignature, String extractedContent, String status, int currentStep) {
        this.id = id;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicantPhone = applicantPhone;
        this.officerName = officerName;
        this.officerEmail = officerEmail;
        this.officerPhone = officerPhone;
        this.documentType = documentType;
        this.filePath = filePath;
        this.fileExtension = fileExtension;
        this.fileSizeKB = fileSizeKB;
        this.digitalSignature = digitalSignature;
        this.extractedContent = extractedContent;
        this.status = status;
        this.currentStep = currentStep;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(id)
                .applicantName(applicantName)
                .applicantEmail(applicantEmail)
                .applicantPhone(applicantPhone)
                .officerName(officerName)
                .officerEmail(officerEmail)
                .officerPhone(officerPhone)
                .documentType(documentType)
                .filePath(filePath)
                .fileExtension(fileExtension)
                .fileSizeKB(fileSizeKB)
                .digitalSignature(digitalSignature)
                .extractedContent(extractedContent)
                .status(status)
                .currentStep(currentStep);
    }

    @Override
    public String toString() {
        return String.format("Hồ sơ [%s] - Nộp bởi: %s - Trạng thái: %s - Bước: %d", id, applicantName, status, currentStep);
    }

    public static class Builder {
        private String id = UUID.randomUUID().toString().substring(0, 8);
        private String applicantName;
        private String applicantEmail;
        private String applicantPhone;
        private String officerName;
        private String officerEmail;
        private String officerPhone;
        private String documentType;
        private String filePath;
        private String fileExtension;
        private long fileSizeKB;
        private String digitalSignature;
        private String extractedContent;
        private String status = "MOI_TAO";
        private int currentStep = 1;

        public String getApplicantName() { return applicantName; }
        public String getApplicantEmail() { return applicantEmail; }
        public String getApplicantPhone() { return applicantPhone; }
        public String getOfficerName() { return officerName; }
        public String getOfficerEmail() { return officerEmail; }
        public String getOfficerPhone() { return officerPhone; }
        public String getDocumentType() { return documentType; }
        public String getFilePath() { return filePath; }
        public String getFileExtension() { return fileExtension; }
        public long getFileSizeKB() { return fileSizeKB; }
        public String getDigitalSignature() { return digitalSignature; }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder applicantName(String applicantName) {
            this.applicantName = applicantName;
            return this;
        }

        public Builder applicantEmail(String applicantEmail) {
            this.applicantEmail = applicantEmail;
            return this;
        }

        public Builder applicantPhone(String applicantPhone) {
            this.applicantPhone = applicantPhone;
            return this;
        }

        public Builder officerName(String officerName) {
            this.officerName = officerName;
            return this;
        }

        public Builder officerEmail(String officerEmail) {
            this.officerEmail = officerEmail;
            return this;
        }

        public Builder officerPhone(String officerPhone) {
            this.officerPhone = officerPhone;
            return this;
        }

        public Builder documentType(String documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public Builder fileSizeKB(long fileSizeKB) {
            this.fileSizeKB = fileSizeKB;
            return this;
        }

        public Builder digitalSignature(String digitalSignature) {
            this.digitalSignature = digitalSignature;
            return this;
        }

        public Builder extractedContent(String extractedContent) {
            this.extractedContent = extractedContent;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder currentStep(int currentStep) {
            this.currentStep = currentStep;
            return this;
        }

        public Builder applicantInfo(String name, String email, String phone) {
            this.applicantName = name;
            this.applicantEmail = email;
            this.applicantPhone = phone;
            return this;
        }

        public Builder officerInfo(String name, String email, String phone) {
            this.officerName = name;
            this.officerEmail = email;
            this.officerPhone = phone;
            return this;
        }

        public Builder attachment(String documentType, String filePath, String fileExtension, long fileSizeKB) {
            this.documentType = documentType;
            this.filePath = filePath;
            this.fileExtension = fileExtension;
            this.fileSizeKB = fileSizeKB;
            return this;
        }

        public Document build() {
            return new Document(id, applicantName, applicantEmail, applicantPhone,
                    officerName, officerEmail, officerPhone,
                    documentType, filePath, fileExtension,
                    fileSizeKB, digitalSignature, extractedContent, status, currentStep);
        }
    }
}