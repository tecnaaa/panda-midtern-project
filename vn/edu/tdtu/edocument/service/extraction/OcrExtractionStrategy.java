package vn.edu.tdtu.edocument.service.extraction;

import vn.edu.tdtu.edocument.service.ocr.AiOcrService;

import java.io.IOException;

public class OcrExtractionStrategy implements ContentExtractionStrategy {
    private final AiOcrService aiOcrService;

    public OcrExtractionStrategy(AiOcrService aiOcrService) {
        this.aiOcrService = aiOcrService;
    }

    @Override
    public String extractText(String filePath, String extension) throws IOException {
        return aiOcrService.extract(filePath, extension);
    }
}
