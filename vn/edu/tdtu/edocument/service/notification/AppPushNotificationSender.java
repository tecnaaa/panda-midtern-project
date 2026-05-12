package vn.edu.tdtu.edocument.service.notification;

import vn.edu.tdtu.edocument.model.Document;

public class AppPushNotificationSender implements NotificationSender {
    @Override
    public void send(NotificationSubscriber subscriber, Document document, String status) {
        System.out.println("  [GUI PUSH]  -> " + subscriber.getDisplayName() + " (" + subscriber.getPushToken()
                + "): Ho so " + document.id + " chuyen sang trang thai " + status);
    }
}
