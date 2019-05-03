package com.guli.poc.functional.follow.impl;

import com.guli.poc.domain.Artist;
import com.guli.poc.domain.Genre;
import com.guli.poc.functional.follow.FavoritePlugin;
import com.guli.poc.functional.follow.FollowedArtistPlugin;
import com.guli.poc.functional.follow.impl.spotify.SpotifyFollowedArtistsPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(PowerMockRunner.class)
public class FavoriteServiceImplTest {

    private FavoritePlugin favoriteService;
    private FollowedArtistPlugin followedArtistPlugin;

    private List<Artist> mockedFilledArtistList;
    
    private String genreClassic = "classic";
    private String genrePop = "pop";
    private String genreRock = "rock";
    private String genreSpiritual = "spiritual";
    
    private Map<String, List<Artist>> mockedGenreArtistsMap;

    private Map<String, List<Artist>> expectedGetFavoriteGenresByFollowedArtistsResult;

    @Before
    public void setUp() throws Exception {
        initMocks();
        favoriteService = new FavoriteServiceImpl(followedArtistPlugin);
    }

    private void initMocks() throws Exception {
        fillSampleData();
        followedArtistPlugin = PowerMockito.spy(new SpotifyFollowedArtistsPlugin(null));
        doReturn(mockedFilledArtistList).when(followedArtistPlugin, "getFollowedArtists", any(), any());
    }

    private void fillSampleData() {
        List<Genre> mockedGenreList1 = Stream.of(new Genre(genreRock), new Genre(genrePop)).collect(Collectors.toList());
        List<Genre> mockedGenreList2 = Stream.of(new Genre(genreRock), new Genre(genrePop), new Genre(genreClassic)).collect(Collectors.toList());
        List<Genre> mockedGenreList3 = Stream.of(new Genre(genreSpiritual), new Genre(genrePop)).collect(Collectors.toList());

        Artist a1 = new Artist("FKA", "1", mockedGenreList1);
        Artist a2 = new Artist("Sevdaliza", "2", mockedGenreList2);
        Artist a3 = new Artist("Muse", "3", mockedGenreList3);

        mockedGenreArtistsMap = new LinkedHashMap<>();
        mockedGenreArtistsMap.put(genrePop, Stream.of(a1, a2).collect(Collectors.toList()));
        mockedGenreArtistsMap.put(genreSpiritual, Stream.of(a2).collect(Collectors.toList()));

        mockedFilledArtistList = Stream.of(a1, a2, a3).collect(Collectors.toList());

        expectedGetFavoriteGenresByFollowedArtistsResult = new LinkedHashMap<>();
        expectedGetFavoriteGenresByFollowedArtistsResult.put(genrePop, Stream.of(a1, a2, a3).collect(Collectors.toList()));
        expectedGetFavoriteGenresByFollowedArtistsResult.put(genreRock, Stream.of(a1, a2).collect(Collectors.toList()));
    }

    @Test
    public void filterArtistsByGenre_returnEmptyList_artistsParameterIsNull() throws Exception {
        // given
        List<Artist> nullList = null;

        // when
        List<Artist> result = Whitebox.invokeMethod(favoriteService, "filterArtistsByGenre", nullList, "name");

        // then
        assertEquals("Expected empty list", 0, result.size());
    }

    @Test
    public void filterArtistsByGenre_returnEmptyList_artistsGenreListDoesNotIncludeGenreName() throws Exception {
        // given
        List<Artist> artists = mockedFilledArtistList;
        String notExistingGenreName = "XasxasxsaxAsdsadd";

        // when
        List<Artist> result = Whitebox.invokeMethod(favoriteService, "filterArtistsByGenre", artists, notExistingGenreName);

        // then
        assertEquals("Expected empty list", 0, result.size());
    }

    @Test
    public void filterArtistsByGenre_returnEmptyList_artistsListIsEmpty() throws Exception {
        // given
        List<Artist> artists = Collections.emptyList();

        // when
        List<Artist> result = Whitebox.invokeMethod(favoriteService, "filterArtistsByGenre", artists, genrePop);

        // then
        assertEquals("Expected empty list", 0, result.size());
    }

    @Test
    public void filterArtistsByGenre_returnFilteredArtists_artistsHasGenreWithGenreName() throws Exception {
        // given
        List<Artist> artists = mockedFilledArtistList;

        // when
        List<Artist> result = Whitebox.invokeMethod(favoriteService, "filterArtistsByGenre", artists, genrePop);

        // then
        assertFalse(result.isEmpty());
        assertEquals("Expected 3 elements list as each artist from artists list (size 3) has it", result.size(), 3);
    }

    @Test
    public void getArtistsGenreNames_returnEmptyList_artistsListIsNull() throws Exception {
        // given
        List<Artist> nullList = null;

        testArtistsGenreNamesReturnEmptyList(nullList);
    }

    @Test
    public void getArtistsGenreNames_returnEmptyList_artistsListIsEmpty() throws Exception {
        // given
        List<Artist> emptyList = Collections.emptyList();

        testArtistsGenreNamesReturnEmptyList(emptyList);
    }

    private void testArtistsGenreNamesReturnEmptyList(List<Artist> artists) throws Exception {
        // when
        Set<String> result = Whitebox.invokeMethod(favoriteService, "getArtistsGenreNames", artists);

        // then
        assertEquals("Expected empty list", 0, result.size());
    }

    @Test
    public void getArtistsGenreNames_returnUniqueGenreNames_artistsListIsFilled() throws Exception {
        // given
        Set<String> expectedNames = Stream.of(genreRock, genrePop, genreSpiritual, genreClassic).collect(Collectors.toSet());

        // when
        Set<String> result = Whitebox.invokeMethod(favoriteService, "getArtistsGenreNames", mockedFilledArtistList);

        // then
        assertEquals(4, result.size());
        assertEquals(expectedNames, result);
    }

    @Test
    public void getArtistsGenreNames_returnEmptyList_artistGenreListIsNull() throws Exception {
        // given
        List<Genre> nulledGenres = null;
        List<Artist> artists = Stream.of(new Artist("Mike", "any", nulledGenres)).collect(Collectors.toList());

        // when
        Set<String> result = Whitebox.invokeMethod(favoriteService, "getArtistsGenreNames", artists);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void groupArtistsByGenreNames_isGrouped() throws Exception {
        // when
        Map<String, List<Artist>> result = Whitebox.invokeMethod(favoriteService, "groupArtistsByGenreNames", mockedFilledArtistList);

        // then
        assertEquals("Expected all unique genres from mockedFilledArtistList", 4, result.size());
        assertEquals("Expected all 3 artists related to pop genre name on a list", 3, result.get(genrePop).size());
        assertEquals("Expected all 2 artists related to rock genre name on a list", 2, result.get(genreRock).size());
        assertEquals("Expected all 2 artists related to classic genre name on a list", 1, result.get(genreClassic).size());
        assertEquals("Expected all 1 artists related to spiritual genre name on a list", 1, result.get(genreSpiritual).size());
    }

    @Test
    public void sortMapByGenrePopularity_mapSorted_asc() throws Exception {
        // when
        Map<String, List<Artist>> result = Whitebox.invokeMethod(favoriteService, "sortMapByGenrePopularity", mockedGenreArtistsMap, true);
        Iterator<Map.Entry<String, List<Artist>>> iterator = result.entrySet().iterator();
        Map.Entry<String, List<Artist>> first = iterator.next();
        Map.Entry<String, List<Artist>> second = iterator.next();

        // then
        assertEquals(genreSpiritual, first.getKey());
        assertEquals(genrePop, second.getKey());
    }

    @Test
    public void getFirstMapItems_return2ElementsMap_givenLimitIsBiggerThanOriginalMap() throws Exception {
        // given
        int hugeNumber = 10000;

        // when
        Map<String, List<Artist>> result = Whitebox.invokeMethod(favoriteService, "getFirstMapItems",
                mockedGenreArtistsMap, hugeNumber);

        // then
        assertEquals("Expected map with size same as param map", mockedGenreArtistsMap.size(), result.size());
    }

    @Test
    public void getFirstMapItems_returnLimited_givenLimitIsNotBiggerThanMapSize() throws Exception {
        // given
        int smallNumber = 1;

        // when
        Map<String, List<Artist>> result = Whitebox.invokeMethod(favoriteService, "getFirstMapItems",
                mockedGenreArtistsMap, smallNumber);

        // then
        assertEquals("Expected limited map", smallNumber, result.size());
    }

    @Test
    public void getFavoriteGenresByGivenArtists_returnSortedMapOfMostPopularGenresWithArtists() throws Exception {
        // given
        int limit = 2;

        // when
        Map<String, List<Artist>> result = Whitebox.invokeMethod(favoriteService, "getFavoriteGenresByGivenArtists",
                mockedFilledArtistList, limit);

        // then
        testGetFavoriteGenresWithArtistsByFollowedArtistsReturnsLititedValidValue(result, limit);
    }

    @Test
    public void getFavoriteGenresByGivenArtists_returnSortedMapOfAllFollowedArtists_limitBiggerThanMapSize() throws Exception {
        // given
        int limit = 2000;
        int genreCount = 4;

        // when
        Map<String, List<Artist>> result = Whitebox.invokeMethod(favoriteService, "getFavoriteGenresByGivenArtists",
                mockedFilledArtistList, limit);

        // then
        testGetFavoriteGenresWithArtistsByFollowedArtistsReturnsLititedValidValue(result, genreCount);
    }

    //AAAA

    @Test
    public void getFavoriteGenresByFollowedArtists_returnSortedMapOfMostPopularGenresWithArtists() throws Exception {
        // given
        int limit = 2;

        // when
        Map<String, List<Artist>> result =  favoriteService.getFavoriteGenresWithArtistsByFollowedArtists("c", "s", limit);

        // then
        testGetFavoriteGenresWithArtistsByFollowedArtistsReturnsLititedValidValue(result, limit);
    }

    @Test
    public void getFavoriteGenresWithArtistsByFollowedArtists_returnSortedMapOfAllFollowedArtists_limitBiggerThanMapSize() throws Exception {
        // given
        int limit = 2000;
        int genreCount = 4;

        // when
        Map<String, List<Artist>> result =  favoriteService.getFavoriteGenresWithArtistsByFollowedArtists("c", "s", limit);

        // then
        testGetFavoriteGenresWithArtistsByFollowedArtistsReturnsLititedValidValue(result, genreCount);
    }

    private void testGetFavoriteGenresWithArtistsByFollowedArtistsReturnsLititedValidValue(Map<String, List<Artist>> result, int expectedSize) {
        Iterator<Map.Entry<String, List<Artist>>> iterator = result.entrySet().iterator();
        Map.Entry<String, List<Artist>> first = iterator.next();
        Map.Entry<String, List<Artist>> second = iterator.next();

        assertEquals(expectedSize, result.size());
        assertEquals(genrePop, first.getKey());
        assertEquals(genreRock, second.getKey());
        assertTrue(first.getValue().stream().anyMatch(a -> a.getName().equals("Muse")));
    }


}