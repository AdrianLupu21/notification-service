package com.smartmug.kafka.publisher;

import com.smartmug.device.configuration.client.error.DeviceConfigurationClientError;
import com.smartmug.kafka.error.KafkaConnectorError;
import com.smartmug.kafka.event.SendTemplateEvent;
import com.smartmug.kafka.producer.KafkaProducerContext;
import com.smartmug.kafka.producer.KafkaProducerContextFactory;
import com.smartmug.kafka.producer.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

import static com.smartmug.kafka.error.KafkaConnectorErrorCode.*;

@Service
public class ForwardTemplateEventPublisher implements ApplicationListener<SendTemplateEvent> {

    @Autowired
    private KafkaProducerContextFactory kafkaProducerContextFactory;

    @Override
    public void onApplicationEvent(SendTemplateEvent event) {
        try {
            final KafkaProducerContext kafkaProducerContext;
            if(null != event.getTopicName() && !event.getTopicName().isEmpty()){
                kafkaProducerContext = kafkaProducerContextFactory.getProducer(ProducerType.ALERT_PRODUCER,
                        event.getTopicName());
            }else {
                kafkaProducerContext = kafkaProducerContextFactory.getProducer(ProducerType.ALERT_PRODUCER);
            }
            kafkaProducerContext.postMessage(event.getTemplateData(), event.getPartitionKey());
            kafkaProducerContext.await();
        } catch (URISyntaxException e){
            throw KafkaConnectorError.newError(DEVICE_CONFIGURATION_URL_ERROR, e.getReason());
        } catch (InterruptedException e) {
            throw KafkaConnectorError.newError(MESSAGE_PROCESSING_ERROR);
        } catch (DeviceConfigurationClientError.DeviceConfigurationClientException e){
            throw KafkaConnectorError.newError(DEVICE_CONFIGURATION_CLIENT_ERROR, String.valueOf(e.getResponseStatus()));
        }

    }
}
