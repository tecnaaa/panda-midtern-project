package vn.edu.tdtu.edocument.service.extraction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TxtExtractionStrategy implements ContentExtractionStrategy {
    @Override
    public String extractText(String filePath, String extension) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }
}
