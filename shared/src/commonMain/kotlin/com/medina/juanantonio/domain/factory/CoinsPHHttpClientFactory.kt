package com.medina.juanantonio.domain.factory

import com.medina.juanantonio.domain.models.SettingsKeys.COINSPH
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import kotlin.time.Clock

class CoinsAuthConfig {
    var apiKeyProvider: (CoinsPHAccount) -> String = { "" }
    var secretProvider: (CoinsPHAccount) -> String = { "" }
}

val CoinsAccountAttribute =
    AttributeKey<CoinsPHAccount>("CoinsPHAccount")

fun HttpRequestBuilder.useAccount(
    account: CoinsPHAccount
) {
    attributes.put(
        CoinsAccountAttribute,
        account
    )
}

val CoinsPHAuthPlugin = createClientPlugin(
    name = "CoinsPHAuthPlugin",
    createConfiguration = ::CoinsAuthConfig
) {
    val apiKeyProvider = pluginConfig.apiKeyProvider
    val secretProvider = pluginConfig.secretProvider

    onRequest { request, _ ->
        val account = request.attributes
            .getOrNull(CoinsAccountAttribute)
            ?: CoinsPHAccount.MAIN

        val apiKey = apiKeyProvider(account)
        val secret = secretProvider(account)

        val timestamp = Clock.System.now()
            .toEpochMilliseconds()
            .toString()

        request.url.parameters.append(
            "timestamp",
            timestamp
        )

        val queryString = request.url.parameters
            .entries()
            .flatMap { (key, values) ->
                values.map { "$key=$it" }
            }
            .joinToString("&")

        val signature = generateSignature(
            queryString,
            secret
        )

        request.url.parameters.append(
            "signature",
            signature
        )

        request.headers.append(
            "X-COINS-APIKEY",
            apiKey
        )
    }
}

class CoinsPHHttpClientFactory(
    private val settings: Settings
) {
    fun create(): HttpClient {
        return HttpClient {
            install(CoinsPHAuthPlugin) {
                apiKeyProvider = { account ->
                    when (account) {
                        CoinsPHAccount.MAIN ->
                            settings.getString(
                                COINSPH.MAIN_API_KEY,
                                ""
                            )

                        CoinsPHAccount.ESCROW ->
                            settings.getString(
                                COINSPH.ESCROW_API_KEY,
                                ""
                            )
                    }
                }

                secretProvider = { account ->
                    when (account) {
                        CoinsPHAccount.MAIN ->
                            settings.getString(
                                COINSPH.MAIN_API_SECRET,
                                ""
                            )

                        CoinsPHAccount.ESCROW ->
                            settings.getString(
                                COINSPH.ESCROW_API_SECRET,
                                ""
                            )
                    }
                }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }

            defaultRequest {
                url("https://api.pro.coins.ph")
            }
        }
    }
}

private fun generateSignature(
    queryString: String,
    secret: String
): String {
    val hmac = HmacSHA256(secret.encodeToByteArray())

    return hmac
        .doFinal(queryString.encodeToByteArray())
        .toHexString()
}