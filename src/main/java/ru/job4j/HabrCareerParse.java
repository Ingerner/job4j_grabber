package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.parse;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK =  "https://career.habr.com";

    private static final String PAGE_LINK = "%s/vacancies/java_developer?page=%s";

    private final DateTimeParser dateTimeParser;

    private static final int PAGE_NAMBER = 5;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }
    public Post post(Element element) throws IOException {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();

        Element dateElement = element.select(".vacancy-card__date").first();
        Element timeElement = dateElement.child(0);
        String vacancyDate = timeElement.attr("datetime");
        String linkVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        HabrCareerDateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        return new Post(vacancyName, linkVacancy,
                retrieveDescription(linkVacancy), new HabrCareerParse()));
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= PAGE_NAMBER; i++) {
            String url = String.format(PAGE_LINK, SOURCE_LINK, i);
            String pageLink = link.replaceAll("=s", Integer.toString(i));
            Connection connection = Jsoup.connect(pageLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                try {
                    posts.add(post(row));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return posts;
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element description = document.select(".style-ugc").first();
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
