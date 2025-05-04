package com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.andredevel.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.andredevel.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import static com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_NAME;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {
    
    private final TemperatureMonitoringService temperatureMonitoringService;
    
    @RabbitListener(queues = QUEUE_NAME, concurrency = "2-3")
    public void handle(@Payload TemperatureLogData temperatureLogData) {
        
        temperatureMonitoringService.processTemperatureReading(temperatureLogData);
    }
}
