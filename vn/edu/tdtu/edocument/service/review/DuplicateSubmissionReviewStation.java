package vn.edu.tdtu.edocument.service.review;

import vn.edu.tdtu.edocument.model.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DuplicateSubmissionReviewStation implements ReviewStation {
    private final Set<String> knownFingerprints = new HashSet<>();

    @Override
    public String getName() {
        return "Kiem tra trung lap ho so";
    }

    @Override
    public void review(Document document) throws ReviewException {
        String fingerprint;
        try {
            fingerprint = buildFingerprint(document.applicantEmail, document.documentType, document.filePath);
        } catch (IOException e) {
            throw new ReviewException("Khong the kiem tra trung lap: " + e.getMessage());
        }

        if (knownFingerprints.contains(fingerprint)) {
            throw new ReviewException("Ho so co noi dung trung lap voi ban nop truoc do.");
        }
    }

    public void registerProcessedDocument(Document document) {
        try {
            String fingerprint = buildFingerprint(document.applicantEmail, document.documentType, document.filePath);
            knownFingerprints.add(fingerprint);
        } catch (IOException ignored) {
        }
    }

    private String buildFingerprint(String applicantEmail, String documentType, String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IOException("File path trong.");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Khong tim thay file: " + filePath);
        }

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        return safeLower(applicantEmail) + "|" + safeLower(documentType) + "|" + sha256(fileBytes);
    }

    private String safeLower(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().toLowerCase(Locale.ROOT);
    }

    private String sha256(byte[] input) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Khong ho tro SHA-256", e);
        }
    }
}
