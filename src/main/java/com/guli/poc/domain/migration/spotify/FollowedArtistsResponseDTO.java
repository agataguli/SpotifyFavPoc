package com.guli.poc.domain.migration.spotify;

import com.guli.poc.domain.Artist;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FollowedArtistsResponseDTO implements Serializable {
    private class InnerDetailsDTO {
        public String next;
        List<ArtistDTO> items;
    }

    InnerDetailsDTO artists;

    public List<Artist> toArtistList() {
        return artists == null || artists.items == null  ? Collections.emptyList() : artists.items.stream().map(ArtistDTO::toArtist).collect(Collectors.toList());
    }

    public String getNext() {
        return artists == null ? null : artists.next;
    }
}
