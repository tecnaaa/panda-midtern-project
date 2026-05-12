package vn.edu.tdtu.edocument.service.storage;

import vn.edu.tdtu.edocument.model.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDocumentStorage implements DocumentStorage {
    private final List<Document> inMemoryTable = new ArrayList<>();

    @Override
    public void save(Document document) throws IOException {
        upsert(document);
        System.out.println("[DATABASE] Da luu ho so " + document.id + " (mock adapter).");
    }

    @Override
    public List<Document> loadAll() throws IOException {
        seedDemoDataIfNeeded();
        System.out.println("[MYSQL MOCK] Dang tai danh sach ho so tu MySQL (gia lap, khong ket noi that).");
        return new ArrayList<>(inMemoryTable);
    }

    private void seedDemoDataIfNeeded() {
        if (!inMemoryTable.isEmpty()) {
            return;
        }

        inMemoryTable.add(new Document(
                "mysql001",
                "Nguyen Van A",
                "vana@example.com",
                "0901000001",
                "Can bo 1",
                "officer1@tdtu.edu.vn",
                "0909000001",
                "DON_XIN_PHEP",
                "mysql://bucket/doc-001.pdf",
                "pdf",
                1200,
                "SIGN_A",
                "Noi dung trich xuat A",
                "DANG_XET_DUYET",
                3,
                "EMAIL,SMS",
                "EMAIL"
        ));

        inMemoryTable.add(new Document(
                "mysql002",
                "Tran Thi B",
                "thib@example.com",
                "0901000002",
                "Can bo 2",
                "officer2@tdtu.edu.vn",
                "0909000002",
                "BAO_CAO",
                "mysql://bucket/doc-002.txt",
                "txt",
                300,
                "SIGN_B",
                "Noi dung trich xuat B",
                "LUU_NHAP",
                2,
                "APP_PUSH",
                "SMS,APP_PUSH"
        ));

        inMemoryTable.add(new Document(
                "mysql003",
                "Le Van C",
                "vanc@example.com",
                "0901000003",
                "Can bo 3",
                "officer3@tdtu.edu.vn",
                "0909000003",
                "HO_SO_THUE",
                "mysql://bucket/doc-003.png",
                "png",
                800,
                "SIGN_C",
                "Noi dung trich xuat C",
                "DA_TIEP_NHAN",
                3,
                "EMAIL,APP_PUSH",
                "EMAIL,SMS,APP_PUSH"
        ));

        inMemoryTable.add(new Document(
                "mysql004",
                "Pham Thi D",
                "thid@example.com",
                "0901000004",
                "Can bo 4",
                "officer4@tdtu.edu.vn",
                "0909000004",
                "BAO_CAO",
                "mysql://bucket/doc-004.pdf",
                "pdf",
                6500,
                "SIGN_D",
                "Noi dung trich xuat D",
                "TU_CHOI",
                3,
                "SMS",
                "EMAIL"
        ));

        inMemoryTable.add(new Document(
                "mysql005",
                "Hoang Van E",
                "vane@example.com",
                "0901000005",
                "Can bo 5",
                "officer5@tdtu.edu.vn",
                "0909000005",
                "DON_XIN_PHEP",
                "mysql://bucket/doc-005.jpg",
                "jpg",
                980,
                "SIGN_E",
                "Noi dung trich xuat E",
                "MOI_TAO",
                1,
                "EMAIL,SMS,APP_PUSH",
                "SMS"
        ));
    }

    private void upsert(Document doc) {
        for (int i = 0; i < inMemoryTable.size(); i++) {
            if (inMemoryTable.get(i).id.equals(doc.id)) {
                inMemoryTable.set(i, doc);
                return;
            }
        }
        inMemoryTable.add(doc);
    }
}
