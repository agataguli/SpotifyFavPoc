package com.guli.poc.domain.repository;

import com.guli.poc.domain.Genre;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository {
    List<Genre> getAvailableGenres();
    void addGenre(Genre genre);
}
