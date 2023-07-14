package org.example.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Parser {

    private final String URL;

    public static final String SCHEDULE_LIST_SELECTOR = "div[class*=schedule__list]";
    public static final String TABLE_MOVIE_ITEM_SELECTOR = "div[class*=schedule__table--movie__item]";
    public static final String CINEMA_SELECTOR = "a[class*=schedule__place-link]";
    public static final String MOVIE_SELECTOR = "div[class*=schedule__event] a[class*=schedule__event-link link]";
    public static final String SPAN_TIME_SELECTOR = "span[class*=schedule__seance-time]";
    public static final String DIV_ITEM_SELECTOR = "a[class*=schedule__seance-time]";

    public Parser(String URL) {
        this.URL = URL;
    }

    public List<Seance> parseSchedule() {
        Document document = getDocument(URL);
        List<Seance> seances = new ArrayList<>();
        if (document != null) {
            seances = getSeances(document);
        }
        return getClosestSeances(seances);
    }

    private Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Seance> getSeances(Document document) {
        List<Seance> seances = new LinkedList<>();
        Element todayScheduleList = document.selectFirst(SCHEDULE_LIST_SELECTOR);
        Elements rows = todayScheduleList != null
                ? todayScheduleList.select(TABLE_MOVIE_ITEM_SELECTOR)
                : new Elements();

        String currentCinema = "";
        for (Element row : rows) {
            if (row.selectFirst(CINEMA_SELECTOR) != null) {
                currentCinema = row.select(CINEMA_SELECTOR).text();
            }

            String filmName = row.select(MOVIE_SELECTOR).text();
            Elements time = row.select(SPAN_TIME_SELECTOR).size() == 0 ?
                    row.select(DIV_ITEM_SELECTOR) :
                    row.select(SPAN_TIME_SELECTOR);
            String finalCurrentCinema = currentCinema;
            time.stream().filter(Objects::nonNull)
                    .map(t -> new Seance(filmName, getSeanceDateTime(t), finalCurrentCinema))
                    .forEach(seances::add);
        }
        return seances;
    }

    private List<Seance> getClosestSeances(List<Seance> seances) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        return seances.stream()
                .filter(seance -> seance.getDateTime().isAfter(dateTimeNow))
                .sorted(Comparator.comparing(Seance::getDateTime))
                .limit(20)
                .toList();
    }

    private static LocalDateTime getSeanceDateTime(Element time) {
        int hour = Integer.parseInt(time.text().split(":")[0]);
        int minute = Integer.parseInt(time.text().split(":")[1]);
        return LocalDate.now().atTime(hour, minute);
    }
}

