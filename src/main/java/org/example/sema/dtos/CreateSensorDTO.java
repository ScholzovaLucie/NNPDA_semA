package org.example.sema.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateSensorDTO {
    private String sensorName;
    private String deviceName;
}
