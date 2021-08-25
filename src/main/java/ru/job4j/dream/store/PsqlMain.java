package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = MemStore.instOf();
        store.save(new Post(0, "Java Job"));
        store.save(new Post(1, "Java Job update"));
        System.out.println("Java Job update".equals(store.findPostById(1).getName()));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }

        store.save(new Candidate(0, "Java Junior developer", 1));
        store.save(new Candidate(1, "Java Junior developer update", 1));
        store.save(new Candidate(0, "Java Middle developer", 1));
        System.out.println(store.findCandidateById(1).getName());
        store.removeCandidateById(1);
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }

        User user = new User();
        user.setName("Admin");
        user.setEmail("root@local");
        user.setPassword("root");
        store.save(user);
        store.save(user);
        User user2 = store.findUserByEmail("root@local");
        System.out.println("User ID: " + user2.getId() + ", User Name: " + user2.getName() + ", User Email: "
        + user2.getEmail() + ", User Password: " + user2.getPassword());

        System.out.println(Timestamp.valueOf(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()));
    }
}
