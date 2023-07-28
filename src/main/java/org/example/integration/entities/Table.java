package org.example.integration.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Table {

    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include. NON_NULL)
    String id;

    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include. NON_NULL)
    String primaryFieldId;

    @JsonIgnoreProperties
    String description;

    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include. NON_NULL)
    List<View> views;

    List<Field> fields;

    String name;
}

