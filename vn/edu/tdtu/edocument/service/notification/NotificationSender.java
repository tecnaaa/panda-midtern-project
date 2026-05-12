package vn.edu.tdtu.edocument.service.notification;

import vn.edu.tdtu.edocument.model.Document;

public interface NotificationSender {
    void send(NotificationSubscriber subscriber, Document document, String status);
}
