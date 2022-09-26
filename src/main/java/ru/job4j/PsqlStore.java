package ru.job4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(cfg.getProperty("jdbc.url"),
                                          cfg.getProperty("jdbc.username"),
                                          cfg.getProperty("jdbc.password"));

    }


    @Override
    public void save(Post post) {
        try(PreparedStatement st = cnn.prepareStatement(
                "insert into posts(name, description, link, created) values(?, ?, ?, ?)")) {
            st.setString(1, post.getTitle());
            st.setString(2, post.getDescription());
            st.setString(3, post.getLink());
            st.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            st.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public List<Post> getAll() {
        return null;
    }

    @Override
    public Post findById(int id) {
        return null;
    }

    public static void main(String[] args) {
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> listPost = habrCareerParse.list("https://career.habr.com/vacancies/java_developer?page=");
        Properties pr = new Properties();
        try(InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("aggregator.properties")) {
            pr.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(PsqlStore psql = new PsqlStore(pr)) {
            listPost.forEach(psql :: save);
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws Exception {

    }
}
