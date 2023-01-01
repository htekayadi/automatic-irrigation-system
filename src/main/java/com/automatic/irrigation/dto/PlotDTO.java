package com.automatic.irrigation.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PlotDTO {
    private String id;
    private String name;
    private Set<String> timeslotIds = new HashSet<>();
    private String sensorId;
}
