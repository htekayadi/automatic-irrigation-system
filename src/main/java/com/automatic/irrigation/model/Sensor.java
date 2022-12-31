package com.automatic.irrigation.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "sensors")
@Data
public class Sensor {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "sensor", cascade = CascadeType.PERSIST)
    private Plot plot;

    @PreRemove
    private void dismissPlot() {
        if (plot != null) {
            plot.setSensor(null);
        }
    }
}
