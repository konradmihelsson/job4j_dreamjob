package ru.job4j.dream.servlet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.PsqlStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

public class CandidateServletTest {

    static Connection connection;

    @BeforeClass
    public static void initConnection() {
        try (InputStream in = CandidateServletTest.class.getClassLoader().getResourceAsStream("db.properties")) {
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
                "TRUNCATE TABLE candidates;"
                        + "ALTER TABLE candidates ALTER COLUMN id RESTART WITH 1;"
        )) {
            ps.execute();
        }
    }

    @Test
    public void whenCreateCandidate() throws IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("0");
        when(req.getParameter("candidateName")).thenReturn("Java Middle Developer");
        when(req.getParameter("candidateCity")).thenReturn("Санкт-Петербург");
        new CandidateServlet().doPost(req, resp);
        Candidate result = PsqlStore.instOf().findAllCandidates().iterator().next();
        assertThat(result.getName(), is("Java Middle Developer"));
    }
}
