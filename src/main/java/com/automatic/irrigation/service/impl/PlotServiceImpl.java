package com.automatic.irrigation.service.impl;

import com.automatic.irrigation.dto.PlotDTO;
import com.automatic.irrigation.model.Plot;
import com.automatic.irrigation.model.Timeslot;
import com.automatic.irrigation.model.builder.PlotBuilder;
import com.automatic.irrigation.repository.PlotRepository;
import com.automatic.irrigation.service.PlotService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PlotServiceImpl implements PlotService {

    @Autowired
    private PlotRepository plotRepository;
    @Autowired
    private PlotBuilder plotBuilder;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<PlotDTO> getAllPlots() {
        List<Plot> plots = plotRepository.findAll();
        return plots.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public PlotDTO addPlot(PlotDTO plotDTO) {
        Plot plot = plotBuilder
                .setId(UUID.randomUUID().toString())
                .setName(plotDTO.getName())
                .build();
        plotRepository.save(plot);

        return convertToDTO(plot);
    }

    @Override
    public PlotDTO updatePlot(String id, PlotDTO plotDTO) {
        Optional<Plot> optionalPlot = plotRepository.findById(id);
        Plot plot;

        if (optionalPlot.isPresent()) {
            plot = optionalPlot.get();
            plot.setName(plotDTO.getName());
            plotRepository.save(plot);
        } else {
            log.error("No plot is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No plot is found with the id: " + plotDTO.getId());
        }
        return convertToDTO(plot);
    }

    @Override
    public PlotDTO getPlot(String id) {
        Optional<Plot> optionalPlot = plotRepository.findById(id);
        if (optionalPlot.isPresent()) {
            return convertToDTO(optionalPlot.get());
        } else {
            log.error("No plot is found with the id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No plot is found with the id: " + id);
        }
    }

    private PlotDTO convertToDTO(Plot plot) {
        PlotDTO plotDTO = modelMapper.map(plot, PlotDTO.class);
        plotDTO.setSensorId(plot.getSensor() != null ? plot.getSensor().getId() : null);
        Set<Timeslot> timeslots = plot.getTimeslots();
        if (timeslots != null) {
            Set<String> slotIds = timeslots.stream().map(Timeslot::getId).collect(Collectors.toSet());
            plotDTO.setTimeslotIds(slotIds);
        }
        return plotDTO;
    }
}
