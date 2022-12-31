package com.automatic.irrigation.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name= "PLOTS")
@Data
public class Plot {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String name;

}
