package vn.edu.tdtu.edocument.service.review;

import vn.edu.tdtu.edocument.model.Document;

public interface ReviewStation {
    String getName();

    void review(Document document) throws ReviewException;
}
