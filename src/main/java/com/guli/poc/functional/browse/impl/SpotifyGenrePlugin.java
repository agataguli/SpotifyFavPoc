package com.guli.poc.functional.browse.impl;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.domain.Genre;
import com.guli.poc.domain.migration.spotify.AvailableGenresResponseDTO;
import com.guli.poc.functional.auth.AuthorizationPlugin;
import com.guli.poc.functional.browse.GenrePlugin;
import com.guli.poc.functional.browse.GenreService;
import com.guli.poc.util.JsonHelper;
import com.guli.poc.util.UnirestUtil;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service(value = "spotify")
public class SpotifyGenrePlugin implements GenrePlugin {

    private final AuthorizationPlugin authorizationPlugin;
    private final GenreService genreService;

    @Autowired
    public SpotifyGenrePlugin(AuthorizationPlugin authorizationPlugin, GenreService genreService) {
        this.authorizationPlugin = authorizationPlugin;
        this.genreService = genreService;
    }

    /**
     * Return genre list if any saved, otherwise send a request to get some
     * @param clientId id of spotify registered application
     * @param secret registered app client secret key
     * @return genre list
     */
    @Override
    public List<Genre> getGenres(String clientId, String secret) {
        List<Genre> existingValues = genreService.getGenres();
        return existingValues.isEmpty() ? this.requestAvailableGenres(clientId, secret) : existingValues;
    }

    /**
     * Send and process request to available-genre-seeds spotify endpoint for available genres
     * @param clientId id of spotify registered application
     * @param secret registered app client secret key
     * @return achieved genre list
     */
    private List<Genre> requestAvailableGenres(String clientId, String secret) {
        AccessToken token = authorizationPlugin.getAuthorizationToken(clientId, secret);
        List<Genre> listToBeFilled = new ArrayList<>();
        try {
            JsonNode jsonNode = UnirestUtil.requestAvailableGenres(token.getSpotifyAuthorizationHeader());
            addAvailableGenresFromNames(getResponseGenreNames(jsonNode));
            listToBeFilled = getStoredGenres();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return listToBeFilled;
    }

    private List<Genre> getStoredGenres() {
        return genreService.getGenres();
    }

    /**
     * Spotify represents genre just as string names. It is required to map this names to domain Genre objects
     * and save them to local repository
     * @param names of genres to be saved
     */
    private void addAvailableGenresFromNames(List<String> names) {
        names.stream().map(Genre::new).forEach(genreService::addGenre);
    }

    private List<String> getResponseGenreNames(JsonNode jsonNode) {
        AvailableGenresResponseDTO dto = JsonHelper.getFromJsonNode(jsonNode, AvailableGenresResponseDTO.class);
        return dto == null || dto.genres == null ? Collections.emptyList() : dto.genres;
    }

}
