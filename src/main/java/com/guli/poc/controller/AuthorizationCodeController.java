package com.guli.poc.controller;

import com.guli.poc.functional.auth.spotify.SpotifyAuthorizationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authcode")
public class AuthorizationCodeController {

    private final SpotifyAuthorizationCodeService spotifyAuthorizationCodeService;

    @Autowired
    public AuthorizationCodeController(SpotifyAuthorizationCodeService spotifyAuthorizationCodeService) {
        this.spotifyAuthorizationCodeService = spotifyAuthorizationCodeService;
    }

    /**
     * This throw authorization code as callback to authorization request
     * @see <a href="https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow"></a>
     * @param code authorization code
     * @param error error message related to failed authorization
     * @param scope state passed by authorization request. Scope which this code enables access to
     */
    @GetMapping("register")
    public void registerAuthorizationCode(@RequestParam(name = "code", required = false) String code,
                                          @RequestParam(name = "error", required = false) String error,
                                          @RequestParam(name = "state", required = false) String scope) {
        if (error == null) {
            spotifyAuthorizationCodeService.addSpotifyAuthorizationCode(scope, code);
        }
    }
}
