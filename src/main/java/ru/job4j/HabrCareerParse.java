package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK =  "https://career.habr.com";

    private static final String PAGE_LINK = "%s/vacancies/java_developer?page=%s";

    private final DateTimeParser dateTimeParser;

    private static final int PAGE_NUMBER = 5;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public Post post(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();

        Element dateElement = element.select(".vacancy-card__date").first();
        Element timeElement = dateElement.child(0);
        String vacancyDate = timeElement.attr("datetime");
        String linkVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        return new Post(
                vacancyName,
                linkVacancy,
                retrieveDescription(linkVacancy),
                dateTimeParser.parse(vacancyDate)
        );
    }

    @Override
    public List<Post> list(String link) {
        try {
            List<Post> posts = new ArrayList<>();
            for (int i = 1; i <= PAGE_NUMBER; i++) {
                //String pageLink = link.substring(0, link.length() - 1) + Integer.toString(i);
                String pageLink = link + i;
                Connection connection = Jsoup.connect(pageLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                        posts.add(post(row));
                });
            }
            return posts;
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private String retrieveDescription(String link) {
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element description = document.select(".style-ugc").first();
            return description.text();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<String> posts = habrCareerParse.list("https://career.habr.com/vacancies/java_developer?page=").stream().map(Post::toString).toList();
        String joinedPosts = String.join(System.lineSeparator(), posts);
        System.out.println(joinedPosts);
    }
}

