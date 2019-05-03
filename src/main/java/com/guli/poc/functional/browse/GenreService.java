package com.guli.poc.functional.browse;

import com.guli.poc.domain.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> getGenres();
    void addGenre(Genre genre);
}
