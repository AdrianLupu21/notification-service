package com.smartmug.device.notification.processor;

import com.smartmug.device.management.client.spi.DeviceManagementClient;
import com.smartmug.device.management.dto.DeviceConnectionProperties;
import com.smartmug.kafka.event.SendTemplateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Service
public class NotificationProcessor {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DeviceManagementClient deviceManagementClient;
    public void postTemplateUpdateNotification(final String templateData,
                                               final String deviceId){
        try {
            final DeviceConnectionProperties deviceConnectionProperties =
                    deviceManagementClient.getDeviceConnectionProperties(deviceId);

        //TODO make a builder for this constructor
        final SendTemplateEvent sendTemplateEvent = new SendTemplateEvent(this, templateData,
                deviceConnectionProperties.getDeviceGroupProperties().getGroupName(),
                deviceConnectionProperties.getPartitionId(), deviceConnectionProperties.getDeviceGroupProperties().getTopicName());
        eventPublisher.publishEvent(sendTemplateEvent);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
