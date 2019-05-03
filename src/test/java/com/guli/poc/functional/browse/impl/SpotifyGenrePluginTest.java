package com.guli.poc.functional.browse.impl;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.domain.Genre;
import com.guli.poc.domain.migration.spotify.AvailableGenresResponseDTO;
import com.guli.poc.functional.auth.AuthorizationPlugin;
import com.guli.poc.functional.auth.spotify.SpotifyAuthorizationPlugin;
import com.guli.poc.functional.browse.GenreService;
import com.guli.poc.util.JsonHelper;
import com.guli.poc.util.UnirestUtil;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({SpotifyGenrePlugin.class, JsonHelper.class, UnirestUtil.class, SpotifyGenrePlugin.class})
@RunWith(PowerMockRunner.class)
public class SpotifyGenrePluginTest {

    private SpotifyGenrePlugin spotifyGenrePlugin;
    private AuthorizationPlugin authorizationPlugin;
    private GenreService  genreService;

    private List<Genre> mockedGenreList;

    @Before
    public void setUp() throws Exception {
        initMocks();
        spotifyGenrePlugin = new SpotifyGenrePlugin(authorizationPlugin, genreService);
    }

    private void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(JsonHelper.class);
        PowerMockito.mockStatic(UnirestUtil.class);
        authorizationPlugin = PowerMockito.spy(new SpotifyAuthorizationPlugin(null, null));
        genreService = PowerMockito.spy(new GenreServiceImpl(null));
        doReturn(new AccessToken("value", "type")).when(authorizationPlugin, "getAuthorizationToken", any(), any());
        mockedGenreList = Stream.of(new Genre("jazz"), new Genre("rock")).collect(Collectors.toList());
    }

    @Test
    public void getResponseGenreNames_emptyList_dtoIsNull() throws Exception {
        // given
        AvailableGenresResponseDTO dto = null;
        when(JsonHelper.getFromJsonNode(any(), any())).thenReturn(dto);

        // when
        List<String> result =  Whitebox.invokeMethod(spotifyGenrePlugin, "getResponseGenreNames", new JsonNode("{}"));

        //then
        assertEquals(0, result.size());
    }

    @Test
    public void getResponseGenreNames_emptyList_genresIsNull() throws Exception {
        // given
        AvailableGenresResponseDTO dto = new AvailableGenresResponseDTO();
        dto.genres = null;
        when(JsonHelper.getFromJsonNode(any(), any())).thenReturn(dto);

        // when
        List<String> result =  Whitebox.invokeMethod(spotifyGenrePlugin, "getResponseGenreNames", new JsonNode("{}"));

        //then
        assertEquals(0, result.size());
    }

    @Test
    public void getResponseGenreNames_returnStringList_genresIsNotNull() throws Exception {
        // given
        AvailableGenresResponseDTO dto = new AvailableGenresResponseDTO();
        dto.genres = Stream.of("jazz", "blues").collect(Collectors.toList());
        when(JsonHelper.getFromJsonNode(any(), any())).thenReturn(dto);

        // when
        List<String> result =  Whitebox.invokeMethod(spotifyGenrePlugin, "getResponseGenreNames", new JsonNode("{}"));

        //then
        assertEquals(dto.genres, result);
    }

    @Test
    public void getResponseGenreNames() throws Exception {
        // given
        AvailableGenresResponseDTO dto = new AvailableGenresResponseDTO();
        dto.genres = Stream.of("jazz", "blues").collect(Collectors.toList());
        when(JsonHelper.getFromJsonNode(any(), any())).thenReturn(dto);

        // when
        List<String> result =  Whitebox.invokeMethod(spotifyGenrePlugin, "getResponseGenreNames", new JsonNode("{}"));

        //then
        assertEquals(dto.genres, result);
    }

    @Test
    public void requestAvailableGenres_returnFilledList_RequestThrowedUnirestException() throws Exception {
        // given
        PowerMockito.when(UnirestUtil.requestAvailableGenres(any())).thenThrow(new UnirestException("meh"));

        // when
        List<Genre> result =  Whitebox.invokeMethod(spotifyGenrePlugin, "requestAvailableGenres", "a", "b");

        // then
        assertEquals(result, Collections.emptyList());
    }

    @Test
    public void requestAvailableGenres_returnValuePassedByGenreService () throws Exception {
        // given
        SpotifyGenrePlugin spy = PowerMockito.spy(spotifyGenrePlugin);
        doNothing().when(spy, "addAvailableGenresFromNames", any());
        doReturn(Collections.emptyList()).when(spy, "getResponseGenreNames", any());
        doReturn(mockedGenreList).when(spy, "getStoredGenres");
        PowerMockito.when(UnirestUtil.requestAvailableGenres(any())).thenReturn(new JsonNode("{}"));


        // when
        List<Genre> result =  Whitebox.invokeMethod(spy, "requestAvailableGenres", "a", "b");

        // then
        assertEquals(result, mockedGenreList);
    }

    @Test
    public void getStoredGenres_returnValuePassedByGenreService () throws Exception {
        // given
        doReturn(mockedGenreList).when(genreService, "getGenres");

        // when
        List<Genre> result =  Whitebox.invokeMethod(spotifyGenrePlugin, "getStoredGenres");

        // then
        assertEquals(result, mockedGenreList);
    }
}