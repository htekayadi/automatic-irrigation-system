package com.automatic.irrigation.model.builder;

import com.automatic.irrigation.constants.Status;
import com.automatic.irrigation.model.Plot;
import com.automatic.irrigation.model.Timeslot;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimeslotBuilder {

    private String id;

    private String name;

    private Long amountOfWater;

    private Instant startTime;

    private Instant endTime;

    private Status status;

    private Plot plot;

    public TimeslotBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TimeslotBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TimeslotBuilder setAmountOfWater(Long amountOfWater) {
        this.amountOfWater = amountOfWater;
        return this;
    }

    public TimeslotBuilder setStartTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public TimeslotBuilder setEndTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public TimeslotBuilder setStatus(Status status) {
        this.status = status;
        return this;
    }

    public TimeslotBuilder setPlot(Plot plot) {
        this.plot = plot;
        return this;
    }

    public Timeslot build() {
        Timeslot slot = new Timeslot();
        slot.setId(id);
        slot.setName(name);
        slot.setAmountOfWater(amountOfWater);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(status);
        slot.setPlot(plot);
        return slot;
    }

}
