package com.medina.juanantonio.data.auth

import com.medina.juanantonio.BuildKonfig
import com.medina.juanantonio.data.auth.OAuthCallbackServer.Companion.AUTH_CALLBACK_ENDPOINT
import com.medina.juanantonio.data.auth.OAuthCallbackServer.Companion.PORT
import kotlinx.coroutines.flow.SharedFlow
import java.awt.Desktop
import java.net.URI

actual class StravaOAuthProvider {
    private val callbackServer = OAuthCallbackServer()

    actual fun getCodes() = callbackServer.codes

    actual fun authenticate() {
        callbackServer.start()

        Desktop
            .getDesktop()
            .browse(URI(getStravaOAuthURL()))
    }

    private fun getStravaOAuthURL(): String {
        val redirectURI = "http://localhost:$PORT$AUTH_CALLBACK_ENDPOINT"
        val url =
            "https://www.strava.com/oauth/authorize" +
                    "?client_id=${BuildKonfig.STRAVA_CLIENT_ID}" +
                    "&response_type=code" +
                    "&redirect_uri=$redirectURI" +
                    "&scope=read,activity:read"

        return url
    }
}
