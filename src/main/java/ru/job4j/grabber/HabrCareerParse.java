package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String pageLink = String.format("%s%s%s", link, "/vacancies/java_developer/?page=", i);
            try {
                Connection connection = Jsoup.connect(pageLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String linkVacancy = String.format("%s%s", link, linkElement.attr("href"));
                    String date = row.select(".basic-date").attr("datetime");
                    rsl.add(new Post(vacancyName, linkVacancy, retrieveDescription(linkVacancy), dateTimeParser.parse(date)));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rsl;
    }

    private String retrieveDescription(String link) {
        String rsl = "";
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element desc = document.select(".vacancy-description__text").first();
            rsl = desc.select(".vacancy-description__text").text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }
}