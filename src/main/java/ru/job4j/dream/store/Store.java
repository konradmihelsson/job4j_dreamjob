package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;
import java.util.List;

public interface Store {

    Collection<Post> findAllPosts();

    Collection<Candidate> findAllCandidates();

    void save(Post post);

    void save(Candidate candidate);

    void save(User user);

    Post findPostById(int id);

    Candidate findCandidateById(int id);

    User findUserByEmail(String email);

    void removeCandidateById(int id);

    User findUserByName(String name);

    int getCityIdByCityName(String cityName);

    List<String> findAllCities();

    Collection<Post> findAllPostsForLastDay();

    Collection<Candidate> findAllCandidatesForLastDay();
}
