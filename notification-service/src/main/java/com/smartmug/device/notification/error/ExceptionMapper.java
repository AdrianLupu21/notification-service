package com.smartmug.device.notification.error;

import com.smartmug.device.configuration.dto.DeviceConfigurationErrorDTO;
import com.smartmug.kafka.error.KafkaConnectorError;
import com.smartmug.kafka.error.KafkaConnectorErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.smartmug.kafka.error.KafkaConnectorErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ExceptionMapper{

    @ExceptionHandler(KafkaConnectorError.KafkaConnectorException.class)
    public ResponseEntity<DeviceConfigurationErrorDTO> handleException(
            final KafkaConnectorError.KafkaConnectorException kafkaConnectorException){
        final Map<KafkaConnectorErrorCode, HttpStatus> errorCodes = mapKafkaErrorCodes();
        DeviceConfigurationErrorDTO deviceConfigurationErrorDTO = new DeviceConfigurationErrorDTO();
        deviceConfigurationErrorDTO.setCode(kafkaConnectorException.getKafkaConnectorErrorCode().name());
        deviceConfigurationErrorDTO.setMessage(kafkaConnectorException.getMessage());
        deviceConfigurationErrorDTO.setTimestamp(Instant.now().toString());
        return new ResponseEntity<>(deviceConfigurationErrorDTO,
                errorCodes.get(kafkaConnectorException.getKafkaConnectorErrorCode()));
    }


    public Map<KafkaConnectorErrorCode, HttpStatus> mapKafkaErrorCodes(){
        final Map<KafkaConnectorErrorCode, HttpStatus> errors = new HashMap<>();
        errors.put(MISSING_MESSAGE_PRODUCER, BAD_REQUEST);
        errors.put(DEVICE_CONFIGURATION_URL_ERROR, INTERNAL_SERVER_ERROR);
        errors.put(DEVICE_CONFIGURATION_CLIENT_ERROR, INTERNAL_SERVER_ERROR);
        errors.put(ERROR_ON_MESSAGE_POST, INTERNAL_SERVER_ERROR);
        errors.put(INVALID_SERIALIZER_RECEIVED, BAD_REQUEST);
        errors.put(MESSAGE_PROCESSING_ERROR, INTERNAL_SERVER_ERROR);
        return errors;
    }
}
