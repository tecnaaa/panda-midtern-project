package vn.edu.tdtu.edocument.service.review;

import vn.edu.tdtu.edocument.model.Document;

import java.io.File;
import java.util.Locale;

public class AntivirusReviewStation implements ReviewStation {
    @Override
    public String getName() {
        return "Quet antivirus";
    }

    @Override
    public void review(Document document) throws ReviewException {
        String extension = document.fileExtension == null ? "" : document.fileExtension.toLowerCase(Locale.ROOT);
        String fileName = new File(document.filePath == null ? "" : document.filePath).getName().toLowerCase(Locale.ROOT);

        if ("exe".equals(extension) || "bat".equals(extension)) {
            throw new ReviewException("File co duoi nguy hiem, bi chan boi antivirus.");
        }

        if (fileName.contains("virus") || fileName.contains("malware") || fileName.contains("trojan")) {
            throw new ReviewException("Phat hien dau hieu tep doc hai trong ten tep.");
        }
    }
}
