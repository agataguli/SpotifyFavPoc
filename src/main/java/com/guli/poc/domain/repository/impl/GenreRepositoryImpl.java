package com.guli.poc.domain.repository.impl;

import com.guli.poc.domain.Genre;
import com.guli.poc.domain.repository.GenreRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreRepositoryImpl implements GenreRepository {
    private List<Genre> genreList = new ArrayList<>();

    @Override
    public List<Genre> getAvailableGenres() {
        return genreList;
    }

    @Override
    public void addGenre(Genre genre) {
        genreList.add(genre);
    }
}
