package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.parse;

public class HabrCareerParse implements Parse {


    private static final String SOURCE_LINK =  "https://career.habr.com";

    private static final String PAGE_LINK = "%s/vacancies/java_developer?page=%s";

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String url = String.format(PAGE_LINK, SOURCE_LINK, i);
            HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();

                Element dateElement = row.select(".vacancy-card__date").first();
                Element timeElement = dateElement.child(0);
                String vacancyDate = timeElement.attr("datetime");
                String link1 = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                try {
                    String description = retrieveDescription(link);
                    posts.add(new Post(vacancyName, link1, description, dateTimeParser.parse(vacancyDate)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        return posts;
    }


    private static String retrieveDescription(String link) throws IOException {
        String linkFormat = "%s" + link;
        String vacancy = String.format(linkFormat, SOURCE_LINK);
        Connection connection = Jsoup.connect(vacancy);
        Document document = connection.get();
        Element row = document.select(".basic-section--appearance-vacancy-description").first();
        Element description = row.select(".collapsible-description").first();
        return description.text();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            String url = String.format(PAGE_LINK, SOURCE_LINK, i);
            HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();

                Element dateElement = row.select(".vacancy-card__date").first();
                Element timeElement = dateElement.child(0);
                String vacancyDate = timeElement.attr("datetime");

                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s %n", vacancyName,  link, parser.parse(vacancyDate));
            });
        }
    }


}
