package com.automatic.irrigation.model.builder;

import com.automatic.irrigation.model.Plot;
import org.springframework.stereotype.Component;

@Component
public class PlotBuilder {

    private String id;

    private String name;

    public PlotBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public PlotBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public Plot build() {
        Plot plot = new Plot();
        plot.setId(id);
        plot.setName(name);

        return plot;
    }

}
