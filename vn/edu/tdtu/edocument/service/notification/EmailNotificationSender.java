package vn.edu.tdtu.edocument.service.notification;

import vn.edu.tdtu.edocument.model.Document;

public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(NotificationSubscriber subscriber, Document document, String status) {
        System.out.println("  [GUI EMAIL] -> " + subscriber.getDisplayName() + " (" + subscriber.getEmail()
                + "): Ho so " + document.id + " chuyen sang trang thai " + status);
    }
}
