package com.smartmug.kafka.producer;

import com.smartmug.kafka.configuration.KafkaConfiguration;

public enum ProducerType {

    ALERT_PRODUCER("kafka/alert-producer.json"),
    STRING_MESSAGE_PRODUCER("kafka/string-message-producer.json"),
    BLOB_MESSAGE_PRODUCER("kafka/blob-message-producer.json"),
    JSON_MESSAGE_PRODUCER("kafka/json-message-producer.json"),
    XML_MESSAGE_PRODUCER("kafka/xml-message-producer.json");

    private KafkaConfiguration kafkaConfiguration;
    private String resourcePath;

    private boolean isConfigCached = false;

    ProducerType(final KafkaConfiguration kafkaConfiguration){
        this.kafkaConfiguration = kafkaConfiguration;
    }

    ProducerType(final String resourcePath){
        this.resourcePath = resourcePath;
    }

    public KafkaConfiguration getKafkaConfiguration() {
        return kafkaConfiguration;
    }

    public void setKafkaConfiguration(final KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
        this.isConfigCached = true;
    }

    public boolean isConfigCached() {
        return isConfigCached;
    }

    public void setConfigCached(final boolean configCached) {
        isConfigCached = configCached;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
