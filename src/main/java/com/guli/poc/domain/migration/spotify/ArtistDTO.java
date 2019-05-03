package com.guli.poc.domain.migration.spotify;

import com.guli.poc.domain.Artist;
import com.guli.poc.domain.Genre;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArtistDTO {
    String name;
    String id;
    List<String> genres;

    public Artist toArtist() {
        List<Genre> genreList = genres == null ? Collections.emptyList() : genres.stream().map(Genre::new).collect(Collectors.toList());
        return new Artist(name, id, genreList);
    }
}
