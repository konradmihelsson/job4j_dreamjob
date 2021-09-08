package ru.job4j.dream.store;

import org.junit.*;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class PsqlStoreTest {

    static Connection connection;

    @BeforeClass
    public static void initConnection() {
        try (InputStream in = PsqlStoreTest.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("jdbc.driver"));
            connection = DriverManager.getConnection(
                    config.getProperty("jdbc.url"),
                    config.getProperty("jdbc.username"),
                    config.getProperty("jdbc.password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @After
    public void wipeTables() throws SQLException {

        try (PreparedStatement ps = connection.prepareStatement(
                "TRUNCATE TABLE post;"
                        + "ALTER TABLE post ALTER COLUMN id RESTART WITH 1;"
                        + "TRUNCATE TABLE candidates;"
                        + "ALTER TABLE candidates ALTER COLUMN id RESTART WITH 1;"
                        + "TRUNCATE TABLE users;"
                        + "ALTER TABLE users ALTER COLUMN id RESTART WITH 1;")
        ) {
            ps.execute();
        }
    }

    @Test
    public void whenFindAllPosts() {
        Store store = PsqlStore.instOf();
        Post post1 = new Post(0, "Junior Java Job");
        Post post2 = new Post(0, "Middle Java Job");
        Post post3 = new Post(0, "Senior Java Job");
        store.save(post1);
        store.save(post2);
        store.save(post3);
        assertThat(store.findAllPosts(), is(List.of(post1, post2, post3)));
    }

    @Test
    public void whenFindUserByEmail() {
        Store store = PsqlStore.instOf();
        User user = new User();
        user.setName("username");
        user.setEmail("username@example.com");
        user.setPassword("Strong_Password!!!11");
        store.save(user);
        user.setId(1);
        User userFromDB = store.findUserByEmail("username@example.com");
        assertThat(userFromDB, is(user));
    }

    @Test
    public void whenFindAllCandidates() {
        Store store = PsqlStore.instOf();
        City city = new City(1, "Санкт-Петербург");
        Candidate candidate1 = new Candidate(0, "Java Junior Developer", city);
        Candidate candidate2 = new Candidate(0, "Java Middle Developer", city);
        Candidate candidate3 = new Candidate(0, "Java Senior Developer", city);
        store.save(candidate1);
        store.save(candidate2);
        store.save(candidate3);
        assertThat(store.findAllCandidates(), is(List.of(candidate1, candidate2, candidate3)));
    }

    @Test
    public void whenCreateCandidate() {
        Store store = PsqlStore.instOf();
        City city = new City(1, "Санкт-Петербург");
        Candidate candidate = new Candidate(0, "Java Developer", city);
        store.save(candidate);
        Candidate candidateInDb = store.findCandidateById(candidate.getId());
        assertThat(candidateInDb.getName(), is(candidateInDb.getName()));
    }

    @Test
    public void whenCreateUser() {
        Store store = PsqlStore.instOf();
        User user = new User();
        user.setName("username");
        user.setEmail("username@example.com");
        user.setPassword("Strong_Password!!!11");
        store.save(user);
        User userInDb = store.findUserByEmail(user.getEmail());
        assertThat(userInDb.getName(), is(user.getName()));
    }

    @Test
    public void whenCreatePost() {
        Store store = PsqlStore.instOf();
        Post post = new Post(0, "Java Job");
        store.save(post);
        Post postInDb = store.findPostById(post.getId());
        assertThat(postInDb.getName(), is(post.getName()));
    }

    @Test
    public void deleteCandidate() {
        Store store = PsqlStore.instOf();
        City city = new City(1, "Санкт-Петербург");
        Candidate candidate1 = new Candidate(0, "Middle Java Developer", city);
        Candidate candidate2 = new Candidate(0, "Senior Java Developer", city);
        store.save(candidate1);
        candidate1.setId(1);
        store.save(candidate2);
        candidate2.setId(2);
        assertThat(store.findAllCandidates(), is(List.of(candidate1, candidate2)));
        store.removeCandidateById(1);
        assertThat(store.findAllCandidates(), is(List.of(candidate2)));
    }
}
