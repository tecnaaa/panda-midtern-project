package vn.edu.tdtu.edocument.service.notification;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public enum NotificationChannel {
    EMAIL,
    SMS,
    APP_PUSH;

    public static Set<NotificationChannel> fromCsv(String rawChannels) {
        EnumSet<NotificationChannel> channels = EnumSet.noneOf(NotificationChannel.class);
        if (rawChannels == null || rawChannels.trim().isEmpty()) {
            channels.add(EMAIL);
            channels.add(SMS);
            return channels;
        }

        String[] parts = rawChannels.split(",");
        for (String part : parts) {
            String normalized = part.trim().toUpperCase(Locale.ROOT);
            if (normalized.isEmpty()) {
                continue;
            }
            try {
                channels.add(NotificationChannel.valueOf(normalized));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (channels.isEmpty()) {
            channels.add(EMAIL);
            channels.add(SMS);
        }
        return channels;
    }
}
