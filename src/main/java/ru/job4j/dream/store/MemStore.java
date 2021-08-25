package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStore implements Store {

    private static final Store INST = new MemStore();
    private static final AtomicInteger POST_ID = new AtomicInteger();
    private static final AtomicInteger CANDIDATE_ID = new AtomicInteger();
    private static final AtomicInteger USER_ID = new AtomicInteger();

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, String> cities = new ConcurrentHashMap<>();

    private MemStore() {
    }

    public static Store instOf() {
        return INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            post.setId(POST_ID.incrementAndGet());
        }
        posts.put(post.getId(), post);
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate.setId(CANDIDATE_ID.incrementAndGet());
        }
        candidates.put(candidate.getId(), candidate);
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            user.setId(USER_ID.incrementAndGet());
        }
        users.put(user.getId(), user);
    }

    @Override
    public Post findPostById(int id) {
        return posts.get(id);
    }

    @Override
    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }

    @Override
    public User findUserByEmail(String email) {
        User result = null;
        for (User user : users.values()) {
            if (email.equals(user.getEmail())) {
                result = user;
                break;
            }
        }
        return result;
    }

    @Override
    public void removeCandidateById(int id) {
        candidates.remove(id);
    }

    @Override
    public User findUserByName(String name) {
        User result = null;
        for (User user : users.values()) {
            if (name.equals(user.getName())) {
                result = user;
                break;
            }
        }
        return result;
    }

    @Override
    public int getCityIdByCityName(String cityName) {
        int result = -1;
        for (Map.Entry<Integer, String> pair : cities.entrySet()) {
            if (cityName.equals(pair.getValue())) {
                result = pair.getKey();
                break;
            }
        }
        return result;
    }

    @Override
    public Map<Integer, String> findAllCities() {
        return cities;
    }

    @Override
    public Collection<Post> findAllPostsForLastDay() {
        return findAllPosts();
    }

    @Override
    public Collection<Candidate> findAllCandidatesForLastDay() {
        return findAllCandidates();
    }
}
