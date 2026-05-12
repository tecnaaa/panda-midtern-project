package vn.edu.tdtu.edocument.service.review;

import vn.edu.tdtu.edocument.model.Document;

import java.util.Set;

public class BasicValidityReviewStation implements ReviewStation {
    private static final long MAX_FILE_SIZE_KB = 5120;
    private final Set<String> supportedExtensions;

    public BasicValidityReviewStation(Set<String> supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }

    @Override
    public String getName() {
        return "Kiem tra hop le co ban";
    }

    @Override
    public void review(Document document) throws ReviewException {
        if (document.fileSizeKB > MAX_FILE_SIZE_KB) {
            throw new ReviewException("Dung luong file " + document.fileSizeKB + "KB vuot qua 5MB.");
        }

        String extension = document.fileExtension == null ? "" : document.fileExtension.trim().toLowerCase();
        if (!supportedExtensions.contains(extension)) {
            throw new ReviewException("Dinh dang " + document.fileExtension + " khong duoc ho tro.");
        }
    }
}
