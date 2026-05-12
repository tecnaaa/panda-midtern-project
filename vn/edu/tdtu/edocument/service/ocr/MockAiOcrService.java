package vn.edu.tdtu.edocument.service.ocr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class MockAiOcrService implements AiOcrService {
    @Override
    public String extract(String filePath, String extension) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File khong ton tai: " + filePath);
        }

        return "[AI_OCR:" + extension.toUpperCase(Locale.ROOT) + "] NOI_DUNG_TRICH_XUAT_TU_" + path.getFileName().toString();
    }
}
