package org.example.sema.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateSensorDTO {
    private String name;
    private String deviceName;

    private String newName;
}
