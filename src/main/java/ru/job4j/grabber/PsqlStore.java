package ru.job4j.grabber;

import ru.job4j.quartz.AlertRabbit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "INSERT INTO grabber.post(name, text, link, created) VALUES(?, ?, ?, ?)"
                + "ON CONFLICT (link) DO NOTHING"
        )) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (Statement ps = cnn.createStatement()) {
            ResultSet resultSet = ps.executeQuery("SELECT * FROM grabber.post");
            while (resultSet.next()) {
                rsl.add(createPost(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        Post rsl = null;
        try (PreparedStatement ps = cnn.prepareStatement("SELECT * FROM grabber.post p WHERE p.id = ?")) {
            ps.setInt(id, 1);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                rsl = createPost(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    private Post createPost(ResultSet resultSet) throws Exception {
        return new Post(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getTimestamp(5).toLocalDateTime());
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = AlertRabbit.loadProperties("grabber.properties");
        try (Store store = new PsqlStore(properties)) {
            store.save(new Post(
                    "Java разработчик",
                    "habr.com/afdsdsf",
                    "SAFDdsaFsadf sa dfsad f",
                    LocalDateTime.now()));
            store.save(new Post(
                    "God Java разработчик",
                    "habr.com/afdsdafdsdsasf",
                    "SAFDdsaFsadf sa dfsad fsad as f",
                    LocalDateTime.now()));
            List<Post> rsl = store.getAll();
            rsl.forEach(System.out::println);
            System.out.println("=========================");
            System.out.println(store.findById(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}