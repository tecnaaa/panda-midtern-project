package vn.edu.tdtu.edocument.service.notification;

import vn.edu.tdtu.edocument.model.Document;

public class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(NotificationSubscriber subscriber, Document document, String status) {
        System.out.println("  [GUI SMS]   -> " + subscriber.getDisplayName() + " (" + subscriber.getPhone()
                + "): Ho so " + document.id + " chuyen sang trang thai " + status);
    }
}
