package org.example.sema.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = { "sensor_name", "device_id" }) // Unikátní kombinace sensorName a device_id
        }
)
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String sensorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    @Getter
    @Setter
    @JsonIgnore
    private Device device;

}
