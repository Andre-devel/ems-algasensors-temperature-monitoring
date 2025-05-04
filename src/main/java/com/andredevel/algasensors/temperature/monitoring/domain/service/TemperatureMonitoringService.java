package com.andredevel.algasensors.temperature.monitoring.domain.service;

import com.andredevel.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.andredevel.algasensors.temperature.monitoring.domain.model.SensorId;
import com.andredevel.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.andredevel.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.andredevel.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.andredevel.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.andredevel.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemperatureMonitoringService {
    
    private final SensorMonitoringRepository sensorMonitoringRepository;
    private final TemperatureLogRepository temperatureLogRepository; 
    
    @Transactional
    public void processTemperatureReading(TemperatureLogData temperatureLogData) {
        sensorMonitoringRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(
                        sensorMonitoring -> handleSensorMonitoring(temperatureLogData, sensorMonitoring),
                        () -> logIgnoredTemperature(temperatureLogData));
    }

    private void logIgnoredTemperature(TemperatureLogData temperatureLogData) {
        log.info("Temperature ignored: SensorId {} Temp {}", temperatureLogData.getId(), temperatureLogData.getValue());
    }


    private void handleSensorMonitoring(TemperatureLogData temperatureLogData, SensorMonitoring sensorMonitoring) {
        if (sensorMonitoring.isEnabled()) {
            sensorMonitoring.setLastTemperature(temperatureLogData.getValue());
            sensorMonitoring.setUpdateAt(OffsetDateTime.now());
            
            sensorMonitoringRepository.saveAndFlush(sensorMonitoring);

            TemperatureLog temperatureLog = TemperatureLog.builder()
                    .id(new TemperatureLogId(temperatureLogData.getId()))
                    .registeredAt(temperatureLogData.getRegisteredAt())
                    .value(temperatureLogData.getValue())
                    .sensorId(new SensorId(temperatureLogData.getSensorId()))
                    .build();
            
            temperatureLogRepository.saveAndFlush(temperatureLog);
            log.info("Temperature Updated: SensorId {} Temp {}", temperatureLogData.getId(), temperatureLogData.getValue());
        } else {
            logIgnoredTemperature(temperatureLogData);
        }
    }
}
