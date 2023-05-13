package com.smartmug.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmug.device.configuration.client.error.DeviceConfigurationClientError;
import com.smartmug.device.configuration.client.spi.DeviceConfigurationClient;
import com.smartmug.kafka.configuration.KafkaConfiguration;
import com.smartmug.kafka.error.KafkaConnectorError;
import com.smartmug.kafka.error.KafkaConnectorErrorCode;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class KafkaProducerContextFactory {

    public static final Logger logger = LoggerFactory.getLogger(KafkaProducerContextFactory.class);

    @Autowired
    private DeviceConfigurationClient deviceConfigurationClient;

    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    public KafkaProducerContext getProducer(final ProducerType producerType) throws URISyntaxException {
            if(!producerType.isConfigCached()){
                fetchConfiguration(producerType);
            }
            return buildProducer(producerType.getKafkaConfiguration());
    }

    public KafkaProducerContext getProducer(final ProducerType producerType, final String topicName) throws URISyntaxException {
        if(!producerType.isConfigCached()){
            fetchConfiguration(producerType);
        }
        producerType.getKafkaConfiguration().setTopicName(topicName);
        return buildProducer(producerType.getKafkaConfiguration());
    }

    private void fetchConfiguration(final ProducerType producerType)
            throws URISyntaxException {
        try {
            final String config = deviceConfigurationClient.getResource(producerType.getResourcePath());
            if(null != config && !config.isEmpty()){
                ObjectMapper objectMapper = new ObjectMapper();
                kafkaConfiguration = objectMapper.readValue(config, KafkaConfiguration.class);
            }
        }catch (final DeviceConfigurationClientError.DeviceConfigurationClientException exception){
            logger.warn("Using default kafka configuration, could not fetch resource {} from device configuration" +
                    " service. Request failed with status code: {}",producerType.getResourcePath(),
                    exception.getResponseStatus());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            producerType.setKafkaConfiguration(kafkaConfiguration);
        }

    }
    private KafkaProducerContext buildProducer(final KafkaConfiguration kafkaConfiguration){
        return KafkaProducerContext.builder()
                .setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBootstrapServers())
                .setProperty(ProducerConfig.ACKS_CONFIG, kafkaConfiguration.getAcks())
                .setProperty(ProducerConfig.RETRIES_CONFIG, kafkaConfiguration.getRetries())
                .setProperty(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfiguration.getBatchSize())
                .setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfiguration.getKeySerializer())
                .setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfiguration.getValueSerializer())
                .setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaConfiguration.getCompressionType().getValue())
                .setTopic(kafkaConfiguration.getTopicName())
                .buildKafkaProducer();
    }

    private Class<?> getClass(final String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw KafkaConnectorError.newError(KafkaConnectorErrorCode.INVALID_SERIALIZER_RECEIVED, className);
        }
    }


}
