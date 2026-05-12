package vn.edu.tdtu.edocument.service.ocr;

import java.io.IOException;

public interface AiOcrService {
    String extract(String filePath, String extension) throws IOException;
}
