package com.guli.poc.functional.auth.spotify;

import com.guli.poc.domain.repository.impl.SpotifyAuthorizationCodeRepository;
import com.guli.poc.functional.auth.AuthorizationCodeService;
import com.guli.poc.util.SpotifyStatics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(PowerMockRunner.class)
public class SpotifyAuthorizationCodeServiceTest {

    private String realScopeValue = SpotifyStatics.Scope.USER_FOLLOW_READ.getScope();
    private AuthorizationCodeService codeService;
    private SpotifyAuthorizationCodeRepository authorizationCodeRepository;
    private String invalidScope = "asdasdSasddsadadaxdsafdsq";

    @Before
    public void setUp() throws Exception {
        initMocks();
        codeService = new SpotifyAuthorizationCodeService(authorizationCodeRepository);
    }

    private void initMocks() throws Exception {
        authorizationCodeRepository = PowerMockito.spy(new SpotifyAuthorizationCodeRepository());
        doNothing().when(authorizationCodeRepository, "addAuthorizationCode", any(), any());
        String mockedAuthorizationCode = "fancyMockedCode";
        doReturn(mockedAuthorizationCode).when(authorizationCodeRepository, "getCode", any());
    }

    @Test
    public void isScopeValid_return_returnTrue_anyScopeEnumHasThisValue() throws Exception {
        // when + then
        assertTrue(Whitebox.invokeMethod(codeService, "isScopeValid", realScopeValue));
    }

    @Test
    public void isScopeValid_return_returnFalse_thereIsNoSuchScopePredefined() throws Exception {
        // when + then
        assertFalse(Whitebox.invokeMethod(codeService, "isScopeValid", invalidScope));
        assertFalse(Arrays.stream(SpotifyStatics.Scope.values()).anyMatch(s -> s.getScope().equals(invalidScope)));
    }

    @Test
    public void isScopeAccessed_returnTrue_scopeValidCodeExistsInRepository() {
        // when + then
        assertTrue(codeService.isScopeAccessed(realScopeValue));
    }

    @Test
    public void isScopeAccessed_returnFalse_scopeIsInvalid() {
        // when + then
        assertFalse(codeService.isScopeAccessed(invalidScope));
    }

    @Test
    public void isScopeAccessed_returnFalse_AuthrorizationCodeIsNull() throws Exception {
        // given
        doReturn(null).when(authorizationCodeRepository, "getCode", realScopeValue);

        // when + then
        assertFalse(codeService.isScopeAccessed(realScopeValue));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSpotifyAuthorizationCode_throwsException_scopeIsInvalid() {
        // when + then
        codeService.addSpotifyAuthorizationCode(invalidScope, "someCode");
    }


    public void addSpotifyAuthorizationCode_doesNotThrowException_scopeIsValid() {
        // when
        codeService.addSpotifyAuthorizationCode(realScopeValue, "someCode");

        // then
        //throws no exception
    }

}