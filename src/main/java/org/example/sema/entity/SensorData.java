package org.example.sema.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "sensor_data",
        indexes = {
                @Index(name = "idx_created_at", columnList = "created_at"),
                @Index(name = "idx_sensor_id", columnList = "sensor_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double value;

    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;
}
