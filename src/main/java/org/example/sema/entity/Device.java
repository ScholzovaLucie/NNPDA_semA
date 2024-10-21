package org.example.sema.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(indexName = "device")
public class Device {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String deviceName;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private Set<String> userIds = new HashSet<>();

    @Field(type = FieldType.Nested)
    @JsonManagedReference
    private List<String> sensorIds = new ArrayList<>();

}
