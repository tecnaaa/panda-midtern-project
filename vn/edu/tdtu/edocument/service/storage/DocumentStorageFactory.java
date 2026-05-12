package vn.edu.tdtu.edocument.service.storage;

public class DocumentStorageFactory {
    public static DocumentStorage create() {
        return new DatabaseDocumentStorage();
    }
}
