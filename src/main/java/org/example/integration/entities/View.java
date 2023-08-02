package org.example.integration.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class View {

    @JsonIgnoreProperties
    String id;

    @JsonIgnoreProperties
    String type;

    String name;
}
