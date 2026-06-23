package com.medina.juanantonio.domain.models.network.caller

import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.strava.StravaApiErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

suspend inline fun <reified T> safeStravaApiCall(
    client: HttpClient,
    crossinline block: suspend HttpClient.() -> HttpResponse
): NetworkResult<T> {
    var bodyText = ""

    return try {
        val response = client.block()
        bodyText = response.bodyAsText()

        when (response.status) {
            in HttpStatusCode.OK..HttpStatusCode.MultiStatus -> {
                val data = response.body<T>()
                NetworkResult.Success(data, response.status.value)
            }

            else -> {
                val errorMessage = parseStravaError(bodyText)
                NetworkResult.Error(
                    message = errorMessage,
                    code = response.status.value
                )
            }
        }
    } catch (e: Exception) {
        val errorMessage = parseCoinsPhError(bodyText)

        NetworkResult.Error(
            message = errorMessage.ifBlank { e.message ?: "Unknown error" },
            throwable = e
        )
    }
}

fun parseStravaError(raw: String): String {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    return try {
        val apiError = json.decodeFromString<StravaApiErrorResponse>(raw)
        apiError.message ?: ""
    } catch (e: Exception) {
        raw.ifBlank { e.message ?: "Unknown server error" }
    }
}
