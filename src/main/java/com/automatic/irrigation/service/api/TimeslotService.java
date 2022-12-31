package com.automatic.irrigation.service.api;

import com.automatic.irrigation.dto.TimeslotDTO;

import java.util.List;

public interface TimeslotService {

    TimeslotDTO addSlot(TimeslotDTO slotDTO);

    TimeslotDTO getSlot(String id);

    TimeslotDTO updateSlot(String id, TimeslotDTO slotDTO);

    List<TimeslotDTO> getAllSlots();

}
