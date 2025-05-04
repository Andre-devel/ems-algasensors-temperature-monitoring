package com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.andredevel.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.andredevel.algasensors.temperature.monitoring.domain.service.SensorAlertService;
import com.andredevel.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import static com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_ALERTING;
import static com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_PROCESS_TEMPERATURE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {
    
    private final TemperatureMonitoringService temperatureMonitoringService;
    private final SensorAlertService sensorAlertService;
    
    @RabbitListener(queues = QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    public void handleProcessTemperature(@Payload TemperatureLogData temperatureLogData) {
        
        temperatureMonitoringService.processTemperatureReading(temperatureLogData);
    }

    @RabbitListener(queues = QUEUE_ALERTING, concurrency = "2-3")
    public void handleAlerting(@Payload TemperatureLogData temperatureLogData) {
        sensorAlertService.handleAlert(temperatureLogData);
        
    }
}
