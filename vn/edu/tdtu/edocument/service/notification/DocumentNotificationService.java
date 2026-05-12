package vn.edu.tdtu.edocument.service.notification;

import vn.edu.tdtu.edocument.model.Document;

import java.util.EnumMap;
import java.util.Map;

public class DocumentNotificationService {
    private final NotificationPreferenceRegistry preferenceRegistry;
    private final Map<NotificationChannel, NotificationSender> senders;

    public DocumentNotificationService() {
        this.preferenceRegistry = new NotificationPreferenceRegistry();
        this.senders = new EnumMap<>(NotificationChannel.class);
        senders.put(NotificationChannel.EMAIL, new EmailNotificationSender());
        senders.put(NotificationChannel.SMS, new SmsNotificationSender());
        senders.put(NotificationChannel.APP_PUSH, new AppPushNotificationSender());
    }

    public void notifyStatusChanged(Document document) {
        preferenceRegistry.syncFromDocument(document);
        for (NotificationSubscriber subscriber : preferenceRegistry.resolveSubscribers(document)) {
            for (NotificationChannel channel : subscriber.getRegisteredChannels()) {
                NotificationSender sender = senders.get(channel);
                if (sender != null) {
                    sender.send(subscriber, document, document.status);
                }
            }
        }
    }
}
