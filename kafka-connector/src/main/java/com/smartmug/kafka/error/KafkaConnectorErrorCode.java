package com.smartmug.kafka.error;

public enum KafkaConnectorErrorCode {

    MISSING_MESSAGE_PRODUCER("The message must not be empty or null"),
    MISSING_PARTITION("There is no partition {0} on topic {1}"),
    DEVICE_CONFIGURATION_URL_ERROR("Error while building URL for device configuration client." +
            "Reason: {}"),
    DEVICE_CONFIGURATION_CLIENT_ERROR("Device client request failed with status {}"),
    ERROR_ON_MESSAGE_POST("Could not send message to topic {} and partition {}"),
    MESSAGE_PROCESSING_ERROR("Error while waiting for the ack of the sent message"),
    INVALID_SERIALIZER_RECEIVED("Serializer {} is not valid");

    private final String message;

    KafkaConnectorErrorCode(final String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
