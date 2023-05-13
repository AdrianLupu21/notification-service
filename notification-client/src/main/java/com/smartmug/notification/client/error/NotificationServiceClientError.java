package com.smartmug.notification.client.error;


public class NotificationServiceClientError {

    public static NotificationServiceClientException newError(final String responseMessage, final int responseStatus){
        return new NotificationServiceClientException(responseMessage, responseStatus);
    }

    public static class NotificationServiceClientException extends RuntimeException{
        private int responseStatus;

        public NotificationServiceClientException(String message, int responseStatus) {
            super(message);
            this.responseStatus = responseStatus;
        }

        public int getResponseStatus() {
            return responseStatus;
        }
    }
}
