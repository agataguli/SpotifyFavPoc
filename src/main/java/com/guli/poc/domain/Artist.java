package com.guli.poc.domain;

import java.util.List;

public class Artist {
    private String name;
    private String id;
    private List<Genre> genres;

    public Artist(String name, String id, List<Genre> genres) {
        this.name = name;
        this.id = id;
        this.genres = genres;
    }

    public String toString() {
        return name + " " + id;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getName() {
        return name;
    }

    public boolean hasGenre(String genre) {
        return genres.stream().map(Genre::getName).anyMatch(g -> g.equals(genre));
    }
}
