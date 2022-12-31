package com.automatic.irrigation.service;


import com.automatic.irrigation.dto.SensorDTO;

import java.util.List;

public interface SensorService {

    SensorDTO addSensor(SensorDTO sensorDTO);

    SensorDTO getSensor(String id);

    SensorDTO updateSensor(String id, SensorDTO sensorDTO);

    List<SensorDTO> getAllSensors();

}
