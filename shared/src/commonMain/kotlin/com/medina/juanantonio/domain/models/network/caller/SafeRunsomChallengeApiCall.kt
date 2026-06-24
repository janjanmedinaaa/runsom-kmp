package com.medina.juanantonio.domain.models.network.caller

import com.medina.juanantonio.domain.models.network.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

suspend inline fun <reified T> safeRunsomChallengeApiCall(
    client: HttpClient,
    crossinline block: suspend HttpClient.() -> HttpResponse
): NetworkResult<T> {
    return try {
        val response = client.block()

        when (response.status) {
            in HttpStatusCode.OK..HttpStatusCode.MultiStatus -> {
                val json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }

                val data = json.decodeFromString<T>(response.bodyAsText())
                NetworkResult.Success(data, response.status.value)
            }

            else -> {
                NetworkResult.Error(
                    message = "Error Occured",
                    code = response.status.value
                )
            }
        }
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.message ?: "Unknown error",
            throwable = e
        )
    }
}
