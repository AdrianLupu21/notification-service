package com.smartmug.kafka.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class SendTemplateEvent extends ApplicationEvent {

    private String templateData;
    private String consumerGroup;
    private int partitionKey;

    private String topicName;

    public SendTemplateEvent(final Object source, final String templateData, final String consumerGroup,
                             final int partitionKey, final String topicName) {
        super(source);
        this.templateData = templateData;
        this.consumerGroup = consumerGroup;
        this.partitionKey = partitionKey;
        this.topicName = topicName;
    }

    public SendTemplateEvent(final Object source, final Clock clock) {
        super(source, clock);
    }

    public String getTemplateData() {
        return templateData;
    }

    public void setTemplateData(final String templateData) {
        this.templateData = templateData;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public int getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(int partitionKey) {
        this.partitionKey = partitionKey;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
