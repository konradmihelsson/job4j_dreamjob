package ru.job4j.dream.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class PsqlStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(PsqlStore.class.getClassLoader()
                        .getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (IOException e) {
            LOG.error("Exception logging", e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            LOG.error("Exception logging", e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT can.id canid, can.name canname, c.id cid,"
                     + "c.name cname FROM candidates can join cities c on c.id = can.city_id")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("canid"), it.getString("canname"),
                            new City(it.getInt("cid"), it.getString("cname"))));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return candidates;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("INSERT INTO post(name, created) VALUES (?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setTimestamp(2,
                    Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return post;
    }

    private void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE post set name = ? where id = ?")
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
    }

    private Candidate create(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("INSERT INTO candidates(name, city_id, created) VALUES (?, ?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity().getId());
            ps.setTimestamp(3,
                    Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return candidate;
    }

    private void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE candidates set name = ?, city_id = ? where id = ?")
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity().getId());
            ps.setInt(3, candidate.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
    }

    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("INSERT INTO users(name, email, password) VALUES (?, ?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return user;
    }

    private void update(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("UPDATE users set name = ?, email = ?, password = ? where id = ?")
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
    }

    @Override
    public Post findPostById(int id) {
        Post result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT id, name FROM post where id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    result = new Post(it.getInt(1), it.getString(2));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return result;
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT can.id canid, can.name canname, c.id cid,"
                     + "c.name cname FROM candidates can join cities c on c.id = can.city_id where can.id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    result = new Candidate(it.getInt("canid"), it.getString("canname"),
                            new City(it.getInt("cid"), it.getString("cname")));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return result;
    }

    @Override
    public User findUserByEmail(String email) {
        String query = "SELECT id, name, email, password FROM users where email = ?";
        return findUserBy(query, email);
    }

    public User findUserByName(String name) {
        String query = "SELECT id, name, email, password FROM users where name = ?";
        return findUserBy(query, name);
    }

    @Override
    public int getCityIdByCityName(String cityName) {
        int result = 0;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT id FROM cities where name = ?")
        ) {
            ps.setString(1, cityName);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    result = it.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return result;
    }

    @Override
    public List<String> findAllCities() {
        List<String> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cities")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    cities.add(it.getString("name"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return cities;
    }

    public void removeCandidateById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM candidates where id = ?")
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
    }

    public Collection<Post> findAllPostsForLastDay() {
        List<Post> posts = new ArrayList<>();
        Timestamp from =
                Timestamp.valueOf(OffsetDateTime.now().minusDays(1).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        Timestamp to =
                Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post WHERE created BETWEEN ? AND ?")
        ) {
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return posts;
    }

    public Collection<Candidate> findAllCandidatesForLastDay() {
        List<Candidate> candidates = new ArrayList<>();
        Timestamp from =
                Timestamp.valueOf(OffsetDateTime.now().minusDays(1).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        Timestamp to =
                Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT can.id canid, can.name canname, c.id cid, "
                     + "c.name cname FROM candidates can join cities c on c.id = can.city_id "
                     + "WHERE can.created BETWEEN ? AND ?")
        ) {
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("canid"), it.getString("canname"),
                            new City(it.getInt("cid"), it.getString("cname"))));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return candidates;
    }

    public User findUserBy(String query, String value) {
        User result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement(query)
        ) {
            ps.setString(1, value);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    result = new User();
                    result.setId(it.getInt(1));
                    result.setName(it.getString(2));
                    result.setEmail(it.getString(3));
                    result.setPassword(it.getString(4));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception logging", e);
        }
        return result;
    }
}
