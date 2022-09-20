package ru.job4j;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    public void close() throws Exception {

    }

    @Override
    public void save(Post post) {

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
        Properties pr = new Properties();
        try(InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("aggregator.properties")) {

        }

    }
}
