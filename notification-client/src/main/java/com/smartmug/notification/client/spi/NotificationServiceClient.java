package com.smartmug.notification.client.spi;

import java.net.URISyntaxException;

public interface NotificationServiceClient {
    void postTemplateUpdateNotification(final byte[] template, final String deviceId) throws URISyntaxException;
}
