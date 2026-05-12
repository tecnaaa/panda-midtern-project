package vn.edu.tdtu.edocument.service.notification;

import vn.edu.tdtu.edocument.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationPreferenceRegistry {
    private final Map<String, Set<NotificationChannel>> preferencesByAccountId = new HashMap<>();

    public void subscribe(String accountId, Set<NotificationChannel> channels) {
        preferencesByAccountId.put(accountId, channels);
    }

    public void unsubscribe(String accountId) {
        preferencesByAccountId.remove(accountId);
    }

    public Set<NotificationChannel> getRegisteredChannels(String accountId, Set<NotificationChannel> defaultChannels) {
        Set<NotificationChannel> channels = preferencesByAccountId.get(accountId);
        if (channels == null || channels.isEmpty()) {
            return defaultChannels;
        }
        return channels;
    }

    public void syncFromDocument(Document document) {
        subscribe(applicantAccountId(document), NotificationChannel.fromCsv(document.applicantNotificationChannels));
        subscribe(officerAccountId(document), NotificationChannel.fromCsv(document.officerNotificationChannels));
    }

    public List<NotificationSubscriber> resolveSubscribers(Document document) {
        List<NotificationSubscriber> subscribers = new ArrayList<>();
        Set<NotificationChannel> applicantDefault = NotificationChannel.fromCsv(document.applicantNotificationChannels);
        Set<NotificationChannel> officerDefault = NotificationChannel.fromCsv(document.officerNotificationChannels);

        subscribers.add(new AccountNotificationSubscriber(
                "Nguoi nop",
                document.applicantEmail,
                document.applicantPhone,
                "applicant:" + document.applicantPhone,
                getRegisteredChannels(applicantAccountId(document), applicantDefault)
        ));

        subscribers.add(new AccountNotificationSubscriber(
                "Can bo tiep nhan",
                document.officerEmail,
                document.officerPhone,
                "officer:" + document.officerPhone,
                getRegisteredChannels(officerAccountId(document), officerDefault)
        ));

        return subscribers;
    }

    private String applicantAccountId(Document document) {
        return "applicant:" + safe(document.applicantEmail);
    }

    private String officerAccountId(Document document) {
        return "officer:" + safe(document.officerEmail);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
