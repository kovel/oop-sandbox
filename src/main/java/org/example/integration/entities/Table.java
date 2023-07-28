package org.example.integration.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Table {

    @JsonIgnoreProperties
    String id;

    @JsonIgnoreProperties
    String primaryFieldId;

    @JsonIgnoreProperties
    String description;

    @JsonIgnoreProperties
    List<View> views;

    List<Field> fields;

    String name;
}

