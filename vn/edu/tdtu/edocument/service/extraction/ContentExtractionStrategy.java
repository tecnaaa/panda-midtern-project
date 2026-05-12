package vn.edu.tdtu.edocument.service.extraction;

import java.io.IOException;

public interface ContentExtractionStrategy {
    String extractText(String filePath, String extension) throws IOException;
}
