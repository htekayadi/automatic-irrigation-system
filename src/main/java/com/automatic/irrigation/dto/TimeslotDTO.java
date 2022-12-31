package com.automatic.irrigation.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Objects;

@Data
public class TimeslotDTO {
    private String id;
    private String name;
    private Long amountOfWater;
    private Instant startTime;
    private Instant endTime;
    private String plotId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeslotDTO timeslotDTO = (TimeslotDTO) o;
        return Objects.equals(id, timeslotDTO.id) && Objects.equals(name, timeslotDTO.name) && Objects.equals(amountOfWater, timeslotDTO.amountOfWater) && Objects.equals(startTime, timeslotDTO.startTime) && Objects.equals(endTime, timeslotDTO.endTime) && Objects.equals(plotId, timeslotDTO.plotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, amountOfWater, startTime, endTime, plotId);
    }
}
