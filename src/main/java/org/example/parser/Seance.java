package org.example.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Seance {

    private String filmName;
    private LocalDateTime dateTime;
    private String cinema;

    public Seance(String filmName, LocalDateTime dateTime, String cinema) {
        this.filmName = filmName;
        this.dateTime = dateTime;
        this.cinema = cinema;
    }

    public String getFilmName() {
        return filmName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCinema() {
        return cinema;
    }

    public void setFilmName(String fileName) {
        this.filmName = filmName;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
    }

    @Override
    public String toString() {
        return filmName +
                " : " + dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) +
                " : " + cinema;
    }
}
