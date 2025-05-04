package com.andredevel.algasensors.temperature.monitoring.domain.service;

import com.andredevel.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.andredevel.algasensors.temperature.monitoring.domain.model.SensorId;
import com.andredevel.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorAlertService {
    
    private final SensorAlertRepository sensorAlertRepository;
    
    @Transactional
    public void handleAlert(TemperatureLogData temperatureLogData) {
        sensorAlertRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(sensorAlert -> {
                    if (sensorAlert.getMaxTemperature() != null && temperatureLogData.getValue().compareTo(sensorAlert.getMaxTemperature()) >= 0) {
                        log.info("Alert Max temperature : sensorId {} , Temperature {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    } else if (sensorAlert.getMinTemperature() != null && temperatureLogData.getValue().compareTo(sensorAlert.getMinTemperature()) <= 0) {
                        log.info("Alert Min temperature : sensorId {} , Temperature {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    } else {
                        logIgnoreAlert(temperatureLogData);
                    }
                     
                }, () -> {
                    logIgnoreAlert(temperatureLogData);
                });
        
    }

    private static void logIgnoreAlert(TemperatureLogData temperatureLogData) {
        log.info("Alert Ignored : sensorId {} , Temperature {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
    }
}
