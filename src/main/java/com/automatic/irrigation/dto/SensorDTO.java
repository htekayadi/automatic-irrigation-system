package com.automatic.irrigation.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SensorDTO {
    private String id;
    private String name;
    private String plotId;
}
