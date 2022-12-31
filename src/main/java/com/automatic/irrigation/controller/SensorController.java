package com.automatic.irrigation.controller;

import com.automatic.irrigation.constants.ErrorMessage;
import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.dto.SensorDTO;
import com.automatic.irrigation.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(RequestURI.API + RequestURI.V1 + RequestURI.SENSORS)
public class SensorController {

    private static final String ID = "/{id}";

    @Autowired
    private SensorService sensorService;

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@RequestBody SensorDTO sensorDTO) {
        SensorDTO sensor = sensorService.addSensor(sensorDTO);
        if (sensor == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.CREATE_FAILED.getValue());
        }

        return new ResponseEntity<>(sensor, HttpStatus.CREATED);
    }

    @GetMapping(value = ID)
    public ResponseEntity<SensorDTO> getSensor(@PathVariable String id) {
        SensorDTO sensor = sensorService.getSensor(id);
        if (sensor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(sensor);
    }

    @PutMapping(value = ID)
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable String id, @RequestBody SensorDTO sensorDTO) {
        SensorDTO sensor = sensorService.updateSensor(id, sensorDTO);
        if (sensor == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.CREATE_FAILED.getValue());
        }

        return new ResponseEntity<>(sensor, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensors() {
        List<SensorDTO> sensors = sensorService.getAllSensors();
        if (sensors == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(sensors);
    }
}
