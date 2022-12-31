package com.automatic.irrigation.service.impl;

import com.automatic.irrigation.dto.SensorDTO;
import com.automatic.irrigation.model.Plot;
import com.automatic.irrigation.model.Sensor;
import com.automatic.irrigation.model.builder.SensorBuilder;
import com.automatic.irrigation.repository.PlotRepository;
import com.automatic.irrigation.repository.SensorRepository;
import com.automatic.irrigation.service.SensorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SensorServiceImpl implements SensorService {

    private static final Logger logger = LogManager.getLogger(SensorServiceImpl.class);

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private PlotRepository plotRepository;
    @Autowired
    private SensorBuilder sensorBuilder;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public SensorDTO addSensor(SensorDTO sensorDTO) {
        Sensor sensor = sensorBuilder
                .setId(UUID.randomUUID().toString())
                .setName(sensorDTO.getName())
                .build();

        Plot plot = getPlot(sensorDTO.getPlotId());
        plot.setSensor(sensor);
        plotRepository.save(plot);

        sensor.setPlot(plot);
        sensorRepository.save(sensor);

        return convertToDTO(sensor);
    }

    @Override
    public SensorDTO updateSensor(String id, SensorDTO sensorDTO) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorDTO.getId());
        if (optionalSensor.isPresent()) {
            Sensor sensor = optionalSensor.get();
            sensor.setName(sensorDTO.getName());

            Plot plot = getPlot(sensorDTO.getPlotId());
            plot.setSensor(sensor);
            plotRepository.save(plot);

            sensor.setPlot(plot);
            sensorRepository.save(sensor);
        } else {
            logger.error("No sensor is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No sensor is found with the id: " + id);
        }
        return sensorDTO;
    }

    @Override
    public SensorDTO getSensor(String id) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        if (optionalSensor.isPresent()) {
            return convertToDTO(optionalSensor.get());
        } else {
            logger.error("No sensor is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No sensor is found with the id: " + id);
        }
    }

    @Override
    public List<SensorDTO> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private SensorDTO convertToDTO(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    private Plot getPlot(String id) {
        Optional<Plot> optionalPlot = plotRepository.findById(id);
        if (optionalPlot.isPresent()) {
            return optionalPlot.get();
        } else {
            logger.error("No plot is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No plot is found with the id: " + id);
        }
    }
}
