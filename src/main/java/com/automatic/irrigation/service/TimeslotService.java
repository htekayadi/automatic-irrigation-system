package com.automatic.irrigation.service;

import com.automatic.irrigation.constants.Status;
import com.automatic.irrigation.dto.SensorDTO;
import com.automatic.irrigation.dto.TimeslotDTO;

import java.util.List;

public interface TimeslotService {

    TimeslotDTO addSlot(TimeslotDTO slotDTO);

    TimeslotDTO getSlot(String id);

    TimeslotDTO updateSlot(String id, TimeslotDTO timeslotDTO);

    TimeslotDTO updateStatus(String id, Status status);

    List<TimeslotDTO> getAllSlots();

}
