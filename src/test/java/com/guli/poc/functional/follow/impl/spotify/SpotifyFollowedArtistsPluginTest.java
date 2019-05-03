package com.guli.poc.functional.follow.impl.spotify;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.domain.Artist;
import com.guli.poc.domain.migration.spotify.FollowedArtistsResponseDTO;
import com.guli.poc.functional.auth.spotify.SpotifyAuthorizationPlugin;
import com.guli.poc.functional.follow.FollowedArtistPlugin;
import com.guli.poc.util.JsonHelper;
import com.guli.poc.util.SpotifyStatics;
import com.guli.poc.util.UnirestUtil;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpotifyFollowedArtistsPlugin.class, SpotifyAuthorizationPlugin.class, UnirestUtil.class})
public class SpotifyFollowedArtistsPluginTest {

    private String mockedStringJsonWithNextUrlWithOneArtist = "{\n" +
            "  \"artists\": {\n" +
            "    \"next\": \"funnyValue\",\n" +
            "    \"items\": [{\"name\": \"Maria Eilish\"}]\n" +
            "  }\n" +
            "}";

    private String mockedStringJsonWithoutNextUrlWithOneArtist = "{\n" +
            "  \"artists\": {\n" +
            "    \"next\": null,\n" +
            "    \"items\": [{\"name\": \"Billie Eilish\"}]\n" +
            "  }\n" +
            "}";

    private SpotifyFollowedArtistsPlugin spotifyFollowedArtistsPlugin;

    private SpotifyAuthorizationPlugin authorizationPlugin;

    @Before
    public void setUp() throws UnirestException {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(UnirestUtil.class);
        when(UnirestUtil.requestFollowedArtistsPiece(any(), any())).thenReturn(new JsonNode("{}"));
        authorizationPlugin = PowerMockito.spy(new SpotifyAuthorizationPlugin(null, null));
        spotifyFollowedArtistsPlugin = new SpotifyFollowedArtistsPlugin(authorizationPlugin);
    }

    @Test
    public void getPrimaryUrl_returnsExpectedUrl() throws Exception {
        // given
        String expectedValue = "https://api.spotify.com/v1/me/following?type=artist";

        // when
        String result = Whitebox.invokeMethod(spotifyFollowedArtistsPlugin, "getPrimaryUrl");

        // then
        assertEquals(expectedValue, result);
    }

    @Test(expected = IllegalAccessException.class)
    public void getFollowedArtists_throwsException_scopeNotAccessed() throws Exception {
        // given
        String anyClientId = "clientId", anySecret = "secret";
        doReturn(false).when(authorizationPlugin, "isScopeAccessed", any());

        // when + then
        spotifyFollowedArtistsPlugin.getFollowedArtists(anyClientId, anySecret);
    }

    @Test
    public void getFollowedArtists_backRequestFollowedArtistsResult_isScopeAccessed() throws Exception {
        // given
        List<Artist> mockedArtists = Stream.of(new Artist("random", "id", null)).collect(Collectors.toList());
        String anyClientId = "clientId", anySecret = "secret";
        SpotifyStatics.Scope scope = SpotifyStatics.Scope.USER_FOLLOW_READ;
        AccessToken mockedAccessToken = new AccessToken("value", "type");
        doReturn(true).when(authorizationPlugin, "isScopeAccessed", any());
        doReturn(mockedAccessToken).when(authorizationPlugin, "getScopeAuthorizationToken", any(), any(), any());
        FollowedArtistPlugin spy = PowerMockito.spy(spotifyFollowedArtistsPlugin);

        // when
        doReturn(mockedArtists).when(spy, "requestFollowedArtists", any());
        List<Artist> parentResult = spy.getFollowedArtists(anyClientId, anySecret);

        // then
        assertEquals(mockedArtists, parentResult);
    }

    @Test
    public void getFollowedArtists_requestPerformOnlyOnce_responseDoesNotIncludeNextPieceUrl() throws Exception {
        // given
        JsonNode mockedResponse = new JsonNode(mockedStringJsonWithoutNextUrlWithOneArtist);
        FollowedArtistsResponseDTO mockedDto = JsonHelper.getFromJsonNode(mockedResponse, FollowedArtistsResponseDTO.class);
        int sizeOfSingleResponseList = mockedDto.toArtistList().size();
        FollowedArtistPlugin spy = PowerMockito.spy(spotifyFollowedArtistsPlugin);
        doReturn(mockedDto).when(spy, "requestFollowedArtistsPiece", any(), any());

        // when
        List<Artist> result = Whitebox.invokeMethod(spy, "requestFollowedArtists", "random");

        // then
        assertEquals("Expected same list size, as only one call to requestFollowedArtistsPiece should happen",
                sizeOfSingleResponseList, result.size());
    }

    @Test
    public void getFollowedArtist_requestPerformMultipleTimes_responseIncludesNextPieceUrl() throws Exception {
        // given
        JsonNode responseWithoutNextUrlNode = new JsonNode(mockedStringJsonWithoutNextUrlWithOneArtist);
        JsonNode responseWithNextUrlNode = new JsonNode(mockedStringJsonWithNextUrlWithOneArtist);
        FollowedArtistsResponseDTO responseWithoutNextUrlDto = JsonHelper.getFromJsonNode(responseWithoutNextUrlNode, FollowedArtistsResponseDTO.class);
        FollowedArtistsResponseDTO responseWithNextUrlDto = JsonHelper.getFromJsonNode(responseWithNextUrlNode, FollowedArtistsResponseDTO.class);
        FollowedArtistPlugin spy = PowerMockito.spy(spotifyFollowedArtistsPlugin);

        // to prevent uncontrolled loop
        String mockedPrimaryUrl = "secureThing";
        doReturn(mockedPrimaryUrl).when(spy, "getPrimaryUrl");
        doReturn(responseWithoutNextUrlDto).when(spy, "requestFollowedArtistsPiece", any(), any());
        doReturn(responseWithNextUrlDto).when(spy, "requestFollowedArtistsPiece", ArgumentMatchers.eq(mockedPrimaryUrl), any());

        // when
        List<Artist> result = Whitebox.invokeMethod(spy, "requestFollowedArtists", "random");

        // then
        assertEquals("Expected list containing elements from all responses (2 resp x 1 elem)", 2, result.size());
    }

    @Test
    public void requestFollowedArtistsPiece_shouldReturnEmptiedElementIfRequestFailed_unirestException() throws Exception {
        // given
        when(UnirestUtil.requestFollowedArtistsPiece(any(), any())).thenThrow(new UnirestException("ups"));

        // when
        FollowedArtistsResponseDTO result = Whitebox.invokeMethod(spotifyFollowedArtistsPlugin, "requestFollowedArtistsPiece", "v1", "v2");

        //then
        assertNull(result);
    }

    @Test
    public void requestFollowedArtistsPiece_shouldReturnFollowedArtistsResponseDTO_jsonNodeValid() throws Exception {
        // given
        JsonNode mockedJsonNode = new JsonNode(mockedStringJsonWithNextUrlWithOneArtist);
        when(UnirestUtil.requestFollowedArtistsPiece(any(), any())).thenReturn(mockedJsonNode);

        // when
        FollowedArtistsResponseDTO result = Whitebox.invokeMethod(spotifyFollowedArtistsPlugin, "requestFollowedArtistsPiece", "v1", "v2");

        //then
        assertNotNull(result);
    }
}