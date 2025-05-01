package com.andredevel.algasensors.temperature.monitoring.api.controller;


import com.andredevel.algasensors.temperature.monitoring.api.model.SensorAlertInput;
import com.andredevel.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.andredevel.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.andredevel.algasensors.temperature.monitoring.domain.model.SensorId;
import com.andredevel.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors/{sensorId}")
@RequiredArgsConstructor
public class SensorAlertController {
    
    private final SensorAlertRepository sensorAlertRepository;
    
    @GetMapping("/alert")
    public SensorAlertOutput getDetail(@PathVariable TSID sensorId) {
        SensorAlert sensorAlert = findSensorAlert(sensorId);
        
        return SensorAlertOutput.builder()
                .id(sensorAlert.getSensorId().getValue())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .minTemperature(sensorAlert.getMinTemperature())
                .build();
    }

    @PutMapping("/alert")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody SensorAlertInput sensorAlertInput,
                       @PathVariable TSID sensorId) {
        SensorAlert sensorAlert = findSensorAlertOrDefault(sensorId);
        
        sensorAlert.setMaxTemperature(sensorAlertInput.getMaxTemperature());
        sensorAlert.setMinTemperature(sensorAlertInput.getMinTemperature());
        
        sensorAlertRepository.saveAndFlush(sensorAlert);
    }
    
    @DeleteMapping("/alert")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        SensorAlert sensorAlert = findSensorAlert(sensorId);
        sensorAlertRepository.delete(sensorAlert);
    }

    private SensorAlert findSensorAlertOrDefault(TSID sensorId) {
        return sensorAlertRepository.findById(new SensorId(sensorId))
                .orElse(SensorAlert.builder()
                        .sensorId(new SensorId(sensorId))
                        .maxTemperature(null)
                        .minTemperature(null)
                        .build());
    }

    private SensorAlert findSensorAlert(TSID sensorId) {
        return sensorAlertRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
