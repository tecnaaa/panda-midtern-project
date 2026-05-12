package vn.edu.tdtu.edocument.service.notification;

import java.util.Set;

public class AccountNotificationSubscriber implements NotificationSubscriber {
    private final String displayName;
    private final String email;
    private final String phone;
    private final String pushToken;
    private final Set<NotificationChannel> registeredChannels;

    public AccountNotificationSubscriber(String displayName, String email, String phone, String pushToken,
                                         Set<NotificationChannel> registeredChannels) {
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.pushToken = pushToken;
        this.registeredChannels = registeredChannels;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getPushToken() {
        return pushToken;
    }

    @Override
    public Set<NotificationChannel> getRegisteredChannels() {
        return registeredChannels;
    }
}
