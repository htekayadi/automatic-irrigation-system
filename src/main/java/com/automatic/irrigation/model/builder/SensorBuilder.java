package com.automatic.irrigation.model.builder;

import com.automatic.irrigation.model.Plot;
import com.automatic.irrigation.model.Sensor;
import org.springframework.stereotype.Component;

@Component
public class SensorBuilder {

    private String id;

    private String name;

    private Plot plot;

    public SensorBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public SensorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SensorBuilder setPlot(Plot plot) {
        this.plot = plot;
        return this;
    }

    public Sensor build() {
        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setName(name);

        sensor.setPlot(plot);
        if (plot != null)
            plot.setSensor(sensor);

        return sensor;
    }

}
