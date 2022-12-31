package com.automatic.irrigation.service.impl;

import com.automatic.irrigation.dto.TimeslotDTO;
import com.automatic.irrigation.model.Plot;
import com.automatic.irrigation.model.Timeslot;
import com.automatic.irrigation.model.builder.TimeslotBuilder;
import com.automatic.irrigation.repository.PlotRepository;
import com.automatic.irrigation.repository.TimeslotRepository;
import com.automatic.irrigation.service.TimeslotService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TimeslotServiceImpl implements TimeslotService {

    @Autowired
    private TimeslotRepository timeslotRepository;
    @Autowired
    private TimeslotBuilder timeslotBuilder;
    @Autowired
    private PlotRepository plotRepository;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public TimeslotDTO addSlot(TimeslotDTO timeslotDTO) {
        Timeslot timeslot = timeslotBuilder
                .setId(UUID.randomUUID().toString())
                .setName(timeslotDTO.getName())
                .setWaterRequired(timeslotDTO.getAmountOfWater())
                .setStartTime(timeslotDTO.getStartTime())
                .setEndTime(timeslotDTO.getEndTime())
                .setPlot(getPlot(timeslotDTO.getPlotId()))
                .build();
        timeslotRepository.save(timeslot);

        return convertToDTO(timeslot);
    }

    @Override
    public TimeslotDTO updateSlot(String id, TimeslotDTO timeslotDTO) {
        Optional<Timeslot> optionalSlot = timeslotRepository.findById(id);
        if (optionalSlot.isPresent()) {
            Timeslot timeslot = optionalSlot.get();
            timeslot.setName(timeslotDTO.getName());
            timeslot.setAmountOfWater(timeslotDTO.getAmountOfWater());
            timeslot.setStartTime(timeslotDTO.getStartTime());
            timeslot.setEndTime(timeslotDTO.getEndTime());
            timeslot.setPlot(getPlot(timeslotDTO.getPlotId()));
            timeslotRepository.save(timeslot);
            return convertToDTO(timeslot);
        } else {
            log.error("No timeslot is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No timeslot is found with the id: " + id);
        }
    }

    @Override
    public TimeslotDTO getSlot(String id) {
        Optional<Timeslot> optionalSlot = timeslotRepository.findById(id);
        if (optionalSlot.isPresent()) {
            return convertToDTO(optionalSlot.get());
        } else {
            log.error("No timeslot is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No timeslot is found with the id: " + id);
        }
    }

    @Override
    public List<TimeslotDTO> getAllSlots() {
        List<Timeslot> timeslots = timeslotRepository.findAll();
        return timeslots.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private TimeslotDTO convertToDTO(Timeslot timeslot) {
        return modelMapper.map(timeslot, TimeslotDTO.class);
    }

    private Plot getPlot(String id) {
        if (null == id || id.isEmpty()) {
            log.error("invalid argument");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plot id must not be empty");
        }
        Optional<Plot> optionalPlot = plotRepository.findById(id);
        if (optionalPlot.isPresent()) {
            return optionalPlot.get();
        } else {
            log.error("No plot is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No plot is found with the id: " + id);
        }
    }
}
