package com.medina.juanantonio.domain.factory.plugins

import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.util.AttributeKey
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import kotlin.collections.component1
import kotlin.collections.component2
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

private fun generateSignature(
    queryString: String,
    secret: String
): String {
    val hmac = HmacSHA256(secret.encodeToByteArray())

    return hmac
        .doFinal(queryString.encodeToByteArray())
        .toHexString()
}
