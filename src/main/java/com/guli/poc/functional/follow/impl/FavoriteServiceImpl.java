package com.guli.poc.functional.follow.impl;

import com.guli.poc.domain.Artist;
import com.guli.poc.domain.Genre;
import com.guli.poc.functional.follow.FavoritePlugin;
import com.guli.poc.functional.follow.FollowedArtistPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoritePlugin {

    private final FollowedArtistPlugin followedArtistPlugin;

    @Autowired
    public FavoriteServiceImpl(@Lazy FollowedArtistPlugin followedArtistPlugin) {
        this.followedArtistPlugin = followedArtistPlugin;
    }

    /**
     * Return user favorite genres based on followed artists
     * @param clientId id of application
     * @param secret registered app client secret key
     * @param limit max number of items
     * @return map of genres and related to them artists
     * @throws IllegalAccessException if scope is not already accessed
     */
    @Override
    public Map<String, List<Artist>> getFavoriteGenresWithArtistsByFollowedArtists(String clientId, String secret, int limit) throws IllegalAccessException {
        List<Artist> artists = followedArtistPlugin.getFollowedArtists(clientId, secret);
        return getFavoriteGenresByGivenArtists(artists, limit);
    }

    /**
     * Return user favorite genres based on given artists
     * @param artists to be compared and grouped
     * @param limit max number of items
     * @return map of genres and related to them artists
     */
    private Map<String, List<Artist>> getFavoriteGenresByGivenArtists(List<Artist> artists, int limit) {
        Map<String, List<Artist>> unsortedMap = groupArtistsByGenreNames(artists);
        Map<String, List<Artist>> sortedMap = sortMapByGenrePopularity(unsortedMap, false);
        return sortMapByGenrePopularity(getFirstMapItems(sortedMap, limit), false);
    }

    /**
     * Return first couple of elements of map
     * @param map map to be limited
     * @param limit items to take
     * @return whole map if its size is smaller than given limit or limit-element map
     */
    private Map<String, List<Artist>> getFirstMapItems(Map<String, List<Artist>> map, int limit) {
        if (map.size() < limit) {
            return map;
        }
        return map.entrySet().stream()
                .limit(limit)
                .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }

    /**
     * Sort map based on number of artists related to genre key.
     * @param unsortedMap map to be sorted
     * @param isAsc sort order, false enable to reverse order
     * @return sorted map
     */
    private Map<String, List<Artist>> sortMapByGenrePopularity(Map<String, List<Artist>> unsortedMap, boolean isAsc) {
        int orderFlag = isAsc ? 1 : -1;
        List<Map.Entry<String, List<Artist>>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort(Comparator.comparingInt(o -> orderFlag * o.getValue().size()));
        Map<String, List<Artist>> sortedMap = new LinkedHashMap<>();
        list.forEach(it -> sortedMap.put(it.getKey(), it.getValue()));
        return sortedMap;
    }

    /**
     * Return map of genres names and list of artists represents each genre
     * @param artists list of artists to be grouped by unique genre names
     * @return map
     */
    private Map<String, List<Artist>> groupArtistsByGenreNames(List<Artist> artists) {
        Map<String, List<Artist>> hashMap = new HashMap<>();
        Set<String> genreNames = getArtistsGenreNames(artists);

        genreNames.forEach(genreName -> hashMap.put(genreName, filterArtistsByGenre(artists, genreName)));
        return hashMap;
    }

    /**
     * Return unique genre names from artists
     * @param artists which genres will be collected
     * @return unique genre names set
     */
    private Set<String> getArtistsGenreNames(List<Artist> artists) {
        return artists == null ? Collections.emptySet()
                : artists.stream().filter(a -> a.getGenres() != null)
                .flatMap(a -> a.getGenres().stream()).map(Genre::getName).collect(Collectors.toSet());
    }

    /**
     * Return filtered list of artists representing given genre
     * @param artists list to be filtered
     * @param genreName name of genre what is a filter
     * @return filtered artists list
     */
    private List<Artist> filterArtistsByGenre(List<Artist> artists, String genreName) {
        return artists == null ? Collections.emptyList()
                : artists.stream().filter(a -> a.hasGenre(genreName)).collect(Collectors.toList());
    }
}
