package org.example.sema.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = true)
    @JsonIgnore
    private Device device;

    @JsonProperty("deviceId")
    public Long getDeviceId() {
        return device != null ? device.getId() : null;
    }

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SensorData> data;
}
