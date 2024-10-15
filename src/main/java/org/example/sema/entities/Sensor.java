package org.example.sema.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    @JoinColumn(name = "device_id", nullable = true)
    @JsonIgnore
    private Device device;
}
