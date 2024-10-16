package org.example.sema.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String deviceName;

    @Column()
    private String description;

    @ManyToMany(mappedBy = "devices")
    private Set<ApplicationUser> users = new HashSet<>();

    @OneToMany(mappedBy = "device", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Sensor> sensors = new ArrayList<>();

}
