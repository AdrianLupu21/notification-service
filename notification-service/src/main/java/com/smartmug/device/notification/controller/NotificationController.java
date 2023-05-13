package com.smartmug.device.notification.controller;

import com.smartmug.device.notification.processor.NotificationProcessor;
import com.smartmug.device.notification.resource.DeviceNotificationResource;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/notification/v1")
public class NotificationController implements DeviceNotificationResource {

    @Autowired
    private NotificationProcessor notificationProcessor;

    @Override
    @PreAuthorize("hasRole('USER')")
    public Response postTemplateUpdateNotification(final byte[] template, final String deviceId) {
        final String templateString = new String(template, StandardCharsets.UTF_8);
        notificationProcessor.postTemplateUpdateNotification(templateString, deviceId);
        return Response.ok().build();
    }

    @Override
    public ResponseEntity<byte[]> fetchTemplate() {
        return null;
    }

    @Override
    public Response sendDataToDevice(byte[] data) {
        return null;
    }
}
