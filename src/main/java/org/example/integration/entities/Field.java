package org.example.integration.entities;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Field {

    String description;

    String name;

    String type;

    String id;

    public Field(String description, String name, String type) {
        this.description = description;
        this.name = name;
        this.type = type;
    }
}
