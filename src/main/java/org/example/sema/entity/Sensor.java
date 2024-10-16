package org.example.sema.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String sensorName;

    @Column()
    private String description;

    @ManyToOne
    @JoinColumn(name = "devi" +
            "ce_id", nullable = true)
    @JsonIgnore
    private Device device;
}
