package com.automatic.irrigation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "plots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plot {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "plot", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Timeslot> timeslots = new LinkedHashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    private Sensor sensor;

    @PreRemove
    public void clearSlotsOnDelete() {
        this.timeslots.clear();
    }
}
