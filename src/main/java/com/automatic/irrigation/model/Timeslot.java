package com.automatic.irrigation.model;

import com.automatic.irrigation.constants.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "timeslots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timeslot {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "amount_of_water")
    private Long amountOfWater;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.CONFIGURED;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plot_id")
    @JsonProperty("plot")
    private Plot plot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeslot slot = (Timeslot) o;
        return Objects.equals(id, slot.id) && Objects.equals(name, slot.name) && Objects.equals(amountOfWater, slot.amountOfWater) && Objects.equals(startTime, slot.startTime) && Objects.equals(endTime, slot.endTime) && status == slot.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, amountOfWater, startTime, endTime, status);
    }
}
