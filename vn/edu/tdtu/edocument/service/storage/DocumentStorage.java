package vn.edu.tdtu.edocument.service.storage;

import vn.edu.tdtu.edocument.model.Document;

import java.io.IOException;
import java.util.List;

public interface DocumentStorage {
    void save(Document document) throws IOException;

    List<Document> loadAll() throws IOException;
}
