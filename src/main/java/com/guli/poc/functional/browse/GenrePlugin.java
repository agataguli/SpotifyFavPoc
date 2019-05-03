package com.guli.poc.functional.browse;

import com.guli.poc.domain.Genre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GenrePlugin {
    List<Genre> getGenres(String clientId, String secret);
}
