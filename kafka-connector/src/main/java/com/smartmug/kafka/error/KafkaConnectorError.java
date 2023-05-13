package com.smartmug.kafka.error;

import java.text.MessageFormat;

public class KafkaConnectorError {

    public static KafkaConnectorException newError(final KafkaConnectorErrorCode kafkaConnectorErrorCode,
                                            final String... errorArguments){
        return new KafkaConnectorException(MessageFormat.format(kafkaConnectorErrorCode.getMessage(), errorArguments),
                kafkaConnectorErrorCode);
    }

    public static class KafkaConnectorException extends RuntimeException{

        private final KafkaConnectorErrorCode kafkaConnectorErrorCode;
        public KafkaConnectorException(final String message, final KafkaConnectorErrorCode kafkaConnectorErrorCode) {
            super(message);
            this.kafkaConnectorErrorCode = kafkaConnectorErrorCode;
        }

        public KafkaConnectorErrorCode getKafkaConnectorErrorCode() {
            return kafkaConnectorErrorCode;
        }
    }
}
