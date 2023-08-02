package org.example.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seance {

    private String filmName;
    private String dateTime;
    private String cinema;

    public Seance(String filmName, String dateTime, String cinema) {
        this.filmName = filmName;
        this.dateTime = dateTime;
        this.cinema = cinema;
    }

    @Override
    public String toString() {
        return filmName +
                " : " + dateTime +
                " : " + cinema;
    }
}
