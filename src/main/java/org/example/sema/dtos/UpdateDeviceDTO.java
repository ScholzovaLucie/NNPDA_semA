package org.example.sema.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateDeviceDTO {
    private Long id;
    private String name;
    private String newName;
}
