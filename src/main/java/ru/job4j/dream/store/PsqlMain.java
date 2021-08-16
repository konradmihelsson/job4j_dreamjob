package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.save(new Post(0, "Java Job"));
        store.save(new Post(1, "Java Job update"));
        System.out.println("Java Job update".equals(store.findPostById(1).getName()));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }

        store.save(new Candidate(0, "Java Junior developer"));
        store.save(new Candidate(1, "Java Junior developer update"));
        store.save(new Candidate(0, "Java Middle developer"));
        store.removeCandidateById(1);
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }
    }
}
