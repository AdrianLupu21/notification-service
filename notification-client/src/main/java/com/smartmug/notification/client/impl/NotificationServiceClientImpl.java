package com.smartmug.notification.client.impl;

import com.smartmug.notification.client.error.NotificationServiceClientError;
import com.smartmug.notification.client.spi.NotificationServiceClient;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;

import static com.smartmug.notification.client.impl.DeviceNotificationClientFactory.BASE_URL;

@Service
public class NotificationServiceClientImpl implements NotificationServiceClient {

    @Autowired
    DeviceNotificationClientFactory deviceNotificationClientFactory;

    @Override
    public void postTemplateUpdateNotification(final byte[] template, final String deviceId) throws URISyntaxException {
        final URIBuilder builder = new URIBuilder(BASE_URL + File.separator + "template" +
                File.separator + "update" + File.separator + deviceId);
        final String requestPath = builder.build().toString();
        final Client client = deviceNotificationClientFactory.getClient();
        Entity<byte[]> entity = Entity.entity(template, MediaType.APPLICATION_OCTET_STREAM);
        try(final Response response = client.target(requestPath)
                .request(MediaType.APPLICATION_JSON_TYPE).post(entity)){
            if(response.getStatus() != Response.Status.OK.getStatusCode()){
                throw NotificationServiceClientError.newError(response.getStatusInfo().getReasonPhrase(),
                        response.getStatus());
            }
        }
    }
}
