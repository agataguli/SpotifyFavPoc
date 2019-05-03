package com.guli.poc.functional.browse.impl;

import com.guli.poc.domain.Genre;
import com.guli.poc.domain.repository.GenreRepository;
import com.guli.poc.functional.browse.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepository.getAvailableGenres();
    }

    @Override
    public void addGenre(Genre genre) {
        genreRepository.addGenre(genre);
    }
}
