package com.automatic.irrigation.controller;

import com.automatic.irrigation.constants.ErrorMessage;
import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.dto.PlotDTO;
import com.automatic.irrigation.dto.TimeslotDTO;
import com.automatic.irrigation.service.PlotService;
import com.automatic.irrigation.service.TimeslotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(RequestURI.API + RequestURI.V1 + RequestURI.TIMESLOTS)
public class TimeslotController {

    private static final String ID = "/{id}";

    @Autowired
    private PlotService plotService;
    @Autowired
    private TimeslotService timeslotService;


    @PostMapping
    public ResponseEntity<TimeslotDTO> createSlot(@RequestBody TimeslotDTO slotDTO) {
        PlotDTO plotDTO = plotService.getPlot(slotDTO.getPlotId());
        if (plotDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        TimeslotDTO slot = timeslotService.addSlot(slotDTO);
        if (slot == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.CREATE_FAILED.getValue());
        }

        return new ResponseEntity<>(slot, HttpStatus.CREATED);
    }

    @GetMapping(value = ID)
    public ResponseEntity<TimeslotDTO> getSlot(@PathVariable String id) {
        TimeslotDTO slotDTO = timeslotService.getSlot(id);
        if (slotDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(slotDTO);
    }

    @PutMapping(value = ID)
    public ResponseEntity<TimeslotDTO> updateSlot(@PathVariable String id, @RequestBody TimeslotDTO slotDTO) {
        TimeslotDTO slot = timeslotService.updateSlot(id, slotDTO);
        if (slot == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UPDATE_FAILED.getValue());
        }

        return new ResponseEntity<>(slot, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TimeslotDTO>> getAllSlots() {
        List<TimeslotDTO> slots = timeslotService.getAllSlots();
        if (slots == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(slots);
    }
}
