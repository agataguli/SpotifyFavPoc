# SpotifyFavPoc
This is just a POC of processing some spotify data.

As reference check: https://developer.spotify.com/documentation/web-api/.

## how to run it
1. it is required to register your app in a spotify developer dashboard. Reference: 
https://developer.spotify.com/documentation/general/guides/app-settings/#register-your-app
2. It is required to add link: http://localhost:8888/authcode/register to your app whitelist.
To do this click 'Edit settings' button in your dashboard and add this link under 'Redirect URIs' section.
3. You can try to run it with ``mvn spring-boot:run -Dspotify-client-id=${yourClientID} -Dspotify-secret=${yourSecret}`` 
## other info
It is hardly recommended to avoid multiple scope accessing cause it doesn't supports refresh of spotify access tokens,
as also some ORM would be great but... :D maybe tomorrow.