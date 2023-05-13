package com.smartmug.kafka.producer;

import com.smartmug.kafka.error.KafkaConnectorError;
import com.smartmug.kafka.error.KafkaConnectorErrorCode;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.smartmug.kafka.error.KafkaConnectorErrorCode.MISSING_PARTITION;

public class KafkaProducerContext {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerContext.class);
    private final String topicName;

    private final CountDownLatch latch;

    private final int latchTimeout;

    private final KafkaProducer<?, ?> producer;

    private KafkaProducerContext(final Map<String, Object> configProps, final String topicName, final int latchTimeout){
        this.producer = new KafkaProducer<>(configProps);
        this.topicName = topicName;
        this.latchTimeout = latchTimeout;
        this.latch = new CountDownLatch(1);
    }

    public void postMessage(final Object message, final int partition){
        if(null != message) {
            producer.send(buildProducerRecord(message, partition), handlePost());
        }else {
            throw KafkaConnectorError.newError(KafkaConnectorErrorCode.MISSING_MESSAGE_PRODUCER, "");
        }
    }

    private Callback handlePost(){
        return (metadata, exception) -> {
            if (null == exception) {
                // Message successfully sent, count down the latch
                logger.info("Message successfully posted on topic {}", metadata.topic());
                logger.debug("Message successfully posted on partition {}", metadata.partition());
                latch.countDown();
            } else {
                throw KafkaConnectorError.newError(KafkaConnectorErrorCode.ERROR_ON_MESSAGE_POST, metadata.topic(),
                        String.valueOf(metadata.partition()));
            }
        };
    }

    private ProducerRecord buildProducerRecord(final Object message, final int targetPartitionIndex){
        // Get partition information for the topic
        List<PartitionInfo> partitionInfos = producer.partitionsFor(topicName);

        // Get the partition index for the target partition
        PartitionInfo targetPartitionInfo = filterPartitions(partitionInfos, targetPartitionIndex);

        return new ProducerRecord<>(this.topicName, targetPartitionInfo.partition(),
                null,
                null,
                message);
    }

    private PartitionInfo filterPartitions(final List<PartitionInfo> partitionInfos, final int partition){
        for(PartitionInfo partitionInfo : partitionInfos){
            if(partitionInfo.partition() == partition){
                return partitionInfo;
            }
        }
        throw KafkaConnectorError.newError(MISSING_PARTITION, String.valueOf(partition), topicName);
    }

    public static KafkaProducerBuilder builder(){
        return new KafkaProducerBuilder();
    }

    public String getTopicName() {
        return topicName;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void await() throws InterruptedException {
        logger.debug("Waiting for latch to be count down");
        this.latch.await(latchTimeout, TimeUnit.MILLISECONDS);
    }

    public void closeProducer(){
        this.producer.close();
    }

    static class KafkaProducerBuilder {
        private static final Map<String, Object> configProps = new HashMap<>();

        private String topicName;

        private int latchTimeout = 5000;

        public KafkaProducerBuilder setProperty(final String key, final String property){
            if(checkParameter(key) || checkParameter(property)){
                logger.warn("key or property field is empty property will not be set");
                return this;
            }
            configProps.put(key, property);
            return this;
        }

        private boolean checkParameter(final String parameter){
            return null == parameter || parameter.isEmpty();
        }

        public KafkaProducerBuilder setTopic(final String topicName){
            this.topicName = topicName;
            return this;
        }

        public KafkaProducerBuilder setLatchTimeout(final int latchTimeout){
            this.latchTimeout = latchTimeout;
            return this;
        }

        public KafkaProducerContext buildKafkaProducer(){
            return new KafkaProducerContext(configProps, topicName, latchTimeout);
        }
    }
}
