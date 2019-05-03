package com.guli.poc.functional.auth.spotify;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.domain.migration.spotify.SpotifyAccessTokenDTO;
import com.guli.poc.util.JsonHelper;
import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.MultipartBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({SpotifyAuthorizationTokenService.class, JsonHelper.class})
@RunWith(PowerMockRunner.class)
public class SpotifyAuthorizationTokenServiceTest {

    private SpotifyAuthorizationTokenService tokenService;
    private SpotifyAuthorizationCodeService codeService;

    private SpotifyAccessTokenDTO mockedSpotifyAccessTokenDTO;

    @Before
    public void setUp() throws Exception {
        initMocks();
        tokenService = new SpotifyAuthorizationTokenService(codeService);
    }

    private void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(JsonHelper.class);

        mockedSpotifyAccessTokenDTO = new SpotifyAccessTokenDTO();
        mockedSpotifyAccessTokenDTO.token_type = "MOCKED";
        mockedSpotifyAccessTokenDTO.access_token = "TOKEN";

        when(JsonHelper.getFromJsonNode(any(), any())).thenReturn(mockedSpotifyAccessTokenDTO);
        codeService = PowerMockito.spy(new SpotifyAuthorizationCodeService(null));
        doReturn("someValue").when(codeService, "getSpotifyAuthorizationCode", any());
    }

    @Test
    public void getAuthorizationHeader() throws Exception {
        // given
        SpotifyAuthorizationTokenService spy = PowerMockito.spy(tokenService);
        String mocked64 = "QWFzZHNhZEFTIEh5dXVzeWpUcw==";
        doReturn(mocked64).when(spy, "getBase64EncodedClientIdSecret", anyString(), anyString());
        String expectedValue = "Basic " + mocked64;

        // when
        String result = Whitebox.invokeMethod(spy, "getAuthorizationHeader", "cl", "se");

        // then
        assertEquals(expectedValue, result);
    }

    @Test
    public void convertToAccessTokenObject_returnNull_accessTokenIsNull() throws Exception {
        // given
        SpotifyAccessTokenDTO nulledDto = null;
        JsonNode jsonNode = new JsonNode("{}");
        when(JsonHelper.getFromJsonNode(any(), any())).thenReturn(nulledDto);

        // when + then
        assertNull(Whitebox.invokeMethod(tokenService, "convertToAccessTokenObject", jsonNode));
    }

    @Test
    public void convertToAccessTokenObject_returnAccessToken_accessTokenIsntNull() throws Exception {
        //given
        JsonNode jsonNode = new JsonNode("{}");
        String expectedAuthHeader = mockedSpotifyAccessTokenDTO.token_type + " " + mockedSpotifyAccessTokenDTO.access_token;

        // when
        AccessToken result = Whitebox.invokeMethod(tokenService, "convertToAccessTokenObject", jsonNode);

        // then
        assertNotNull(result);
        assertEquals(expectedAuthHeader, result.getSpotifyAuthorizationHeader());
    }

    @Test
    public void getAccessToken_returnNull_ifConversionThrowsException() throws Exception {
        // given
        SpotifyAuthorizationTokenService spy = PowerMockito.spy(tokenService);
        doThrow(new UnirestException("any")).when(spy, "getSpotifyAccessTokenNode", any());
        MultipartBody body = new MultipartBody(new HttpRequest(HttpMethod.GET, "url"));

        // when
        AccessToken result = Whitebox.invokeMethod(spy, "getAccessToken", body);

        // then
        assertNull(result);
    }

    @Test
    public void getAccessToken_returnAccessToken_spotifyAccessTokenFounded() throws Exception {
        // given
        String sampleAccessTokenNodeString = "{\"access_token\":\"aa\",\"scope\":\"bb\",\"token_type\":\"cc\"}";
        JsonNode mockedJsonNode = new JsonNode(sampleAccessTokenNodeString);
        SpotifyAuthorizationTokenService spy = PowerMockito.spy(tokenService);
        doReturn(mockedJsonNode).when(spy, "getSpotifyAccessTokenNode", any());
        MultipartBody body = new MultipartBody(new HttpRequest(HttpMethod.GET, "url"));
        String expectedHeader = mockedSpotifyAccessTokenDTO.token_type + " " + mockedSpotifyAccessTokenDTO.access_token;

        // when
        AccessToken result = Whitebox.invokeMethod(spy, "getAccessToken", body);

        // then
        assertNotNull(result);
        assertEquals(expectedHeader, result.getSpotifyAuthorizationHeader());
    }

    @Test
    public void requestNonUserResourcesAccessToken_callsGetAccessToken() throws Exception {
        // given
        AccessToken accessTokenReturnedByGetAccessToken = new AccessToken("ANY", "VALUE");
        SpotifyAuthorizationTokenService spy = PowerMockito.spy(tokenService);
        doReturn(accessTokenReturnedByGetAccessToken).when(spy, "getAccessToken", any());
        doReturn(true).when(codeService, "isScopeAccessed", any());

        // when
        AccessToken result = spy.requestNonUserResourcesAccessToken("as41!ASsc", "Asadsade3w");

        // then
        assertEquals("Expected object returned via getAccessToken", accessTokenReturnedByGetAccessToken, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestUserResourcesScopeAccessToken_throwsExpeption_ScopeIsNotAccessed() throws Exception {
        // given
        doReturn(false).when(codeService, "isScopeAccessed", any());

        // when + then
        tokenService.requestUserResourcesScopeAccessToken("a", "b", "c");
    }

    @Test
    public void requestUserResourcesScopeAccessToken_callsGetAccessToken() throws Exception {
        // given
        AccessToken accessTokenReturnedByGetAccessToken = new AccessToken("ANY", "VALUE");
        SpotifyAuthorizationTokenService spy = PowerMockito.spy(tokenService);
        doReturn(accessTokenReturnedByGetAccessToken).when(spy, "getAccessToken", any());
        doReturn(true).when(codeService, "isScopeAccessed", any());

        // when
        AccessToken result = spy.requestUserResourcesScopeAccessToken("x", "y", "z");

        // then
        assertEquals("Expected object returned via getAccessToken", accessTokenReturnedByGetAccessToken, result);
    }

}