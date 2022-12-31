package com.automatic.irrigation.controller;

import com.automatic.irrigation.constants.ErrorMessage;
import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.dto.PlotDTO;
import com.automatic.irrigation.service.PlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(RequestURI.API + RequestURI.V1 + RequestURI.PLOTS)
public class PlotController {

    private static final String ID = "/{id}";

    @Autowired
    private PlotService plotService;

    @PostMapping
    public ResponseEntity<PlotDTO> createPlot(@RequestBody PlotDTO plotDTO) {
        PlotDTO plot = plotService.addPlot(plotDTO);
        if (plot == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorMessage.CREATE_FAILED.getValue());
        }
        return new ResponseEntity<>(plot, HttpStatus.CREATED);
    }

    @GetMapping(value = ID)
    public ResponseEntity<PlotDTO> getPlot(@PathVariable String id) {
        PlotDTO plotDTO = plotService.getPlot(id);
        if (plotDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(plotDTO);
    }

    @PutMapping(value = ID)
    public ResponseEntity<PlotDTO> updatePlot(@PathVariable String id, @RequestBody PlotDTO plotDTO) {
        PlotDTO plot = plotService.updatePlot(id, plotDTO);
        if (plot == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorMessage.UPDATE_FAILED.getValue());
        }
        return new ResponseEntity<>(plot, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PlotDTO>> getAllPlots() {
        List<PlotDTO> plots = plotService.getAllPlots();
        if (plots == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(plots);
    }
}
