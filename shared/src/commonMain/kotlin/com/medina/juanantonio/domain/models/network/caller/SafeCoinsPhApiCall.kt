package com.medina.juanantonio.domain.models.network.caller

import com.medina.juanantonio.domain.factory.plugins.ApiBlockedException
import com.medina.juanantonio.domain.models.network.NetworkResult
import com.medina.juanantonio.domain.models.network.coinsph.CoinsPHApiErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

suspend inline fun <reified T> safeCoinsPhApiCall(
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
                val errorMessage = parseCoinsPhError(bodyText)
                NetworkResult.Error(
                    message = errorMessage,
                    code = response.status.value
                )
            }
        }
    } catch (_: ApiBlockedException) {
        NetworkResult.Error("API temporarily blocked. Try again later.")
    } catch (e: Exception) {
        val errorMessage = parseCoinsPhError(bodyText)

        NetworkResult.Error(
            message = errorMessage.ifBlank { e.message ?: "Unknown error" },
            throwable = e
        )
    }
}

fun parseCoinsPhError(raw: String): String {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    return try {
        val apiError = json.decodeFromString<CoinsPHApiErrorResponse>(raw)
        if (apiError.msg?.isBlank() == true) "" else "Code ${apiError.code}: ${apiError.msg}"

    } catch (e: Exception) {
        raw.ifBlank { e.message ?: "Unknown server error" }
    }
}
