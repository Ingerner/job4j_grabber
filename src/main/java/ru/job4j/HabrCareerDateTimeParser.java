package ru.job4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime localDateTime = LocalDateTime.parse(parse);
        return localDateTime;
    }
}
