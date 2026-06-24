package com.medina.juanantonio.data.network

import com.medina.juanantonio.BuildKonfig
import com.medina.juanantonio.domain.models.network.strava.StravaActivity
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.caller.safeStravaApiCall
import com.medina.juanantonio.domain.models.network.strava.StravaOAuthTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlin.io.encoding.Base64

class StravaRemoteSource(
    private val client: HttpClient
) {

    companion object {
        const val GRANT_AUTHORIZATION_CODE = "authorization_code"
        const val GRANT_REFRESH_TOKEN = "refresh_token"
    }

    suspend fun requestToken(
        code: String
    ): NetworkResult<StravaOAuthTokenResponse> {
        return safeStravaApiCall(client) {
            post("https://www.strava.com/api/v3/oauth/token") {
                contentType(ContentType.Application.FormUrlEncoded)

                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("client_id", BuildKonfig.STRAVA_CLIENT_ID)
                            append("client_secret", BuildKonfig.STRAVA_CLIENT_SECRET)
                            append("code", code)
                            append("grant_type", GRANT_AUTHORIZATION_CODE)
                        }
                    )
                )
            }
        }
    }

    suspend fun refreshToken(
        refreshToken: String
    ): NetworkResult<StravaOAuthTokenResponse> {
        return safeStravaApiCall(client) {
            post("https://www.strava.com/api/v3/oauth/token") {
                contentType(ContentType.Application.FormUrlEncoded)

                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("client_id", BuildKonfig.STRAVA_CLIENT_ID)
                            append("client_secret", BuildKonfig.STRAVA_CLIENT_SECRET)
                            append("grant_type", GRANT_REFRESH_TOKEN)
                            append("refresh_token", refreshToken)
                        }
                    )
                )
            }
        }
    }

    suspend fun getAthleteActivities(
        accessToken: String,
        before: Long,
        after: Long,
        page: Int,
        perPage: Int
    ): NetworkResult<List<StravaActivity>> {
        return safeStravaApiCall(client) {
            get("https://www.strava.com/api/v3/athlete/activities") {
                header("Authorization", "Bearer $accessToken")

                parameter("page", page)
                parameter("per_page", perPage)

                if (before != -1L) parameter("before", before)
                if (after != -1L) parameter("after", after)
            }
        }
    }

    suspend fun revokeToken(
        refreshToken: String
    ): NetworkResult<Unit> {
        return safeStravaApiCall(client) {
            post("https://www.strava.com/oauth/revoke") {
                val authByteArray =
                    "${BuildKonfig.STRAVA_CLIENT_ID}:${BuildKonfig.STRAVA_CLIENT_SECRET}"
                        .encodeToByteArray()

                val authorizationToken = Base64.encode(authByteArray)
                header("Authorization", "Basic $authorizationToken")
                contentType(ContentType.Application.FormUrlEncoded)

                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("token", refreshToken)
                        }
                    )
                )
            }
        }
    }
}
