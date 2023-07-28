package org.example.integration.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Field {

    String description;

    String name;

    String type;

    @JsonInclude(JsonInclude.Include. NON_NULL)
    String id;

    public Field(String description, String name, String type) {
        this.description = description;
        this.name = name;
        this.type = type;
    }
}
