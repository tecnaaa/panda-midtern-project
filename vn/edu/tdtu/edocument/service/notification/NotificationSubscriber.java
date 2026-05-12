package vn.edu.tdtu.edocument.service.notification;

import java.util.Set;

public interface NotificationSubscriber {
    String getDisplayName();

    String getEmail();

    String getPhone();

    String getPushToken();

    Set<NotificationChannel> getRegisteredChannels();
}
