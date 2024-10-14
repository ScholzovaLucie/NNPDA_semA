package org.example.sema.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"device_name", "user_id"})})
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String deviceName;

    @ManyToMany(mappedBy = "devices")
    @Getter
    @Setter
    @JsonIgnore
    private Set<ApplicationUser> users = new HashSet<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Sensor> sensors = new ArrayList<>();

}
