package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

class HabrCareerDateTimeParserTest {

    @Test
    public void whenParseDate() {
        String dateStr = "2023-11-01T15:53:59+03:00";
        LocalDateTime expected = LocalDateTime.parse("2023-11-01T15:53:59");
        LocalDateTime result = new HabrCareerDateTimeParser().parse(dateStr);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void whenParseDate2() {
        String dateStr = "2023-11-02T10:13:52+03:00";
        LocalDateTime expected = LocalDateTime.parse("2023-11-02T10:13:52");
        LocalDateTime result = new HabrCareerDateTimeParser().parse(dateStr);
        assertThat(result).isEqualTo(expected);
    }
}