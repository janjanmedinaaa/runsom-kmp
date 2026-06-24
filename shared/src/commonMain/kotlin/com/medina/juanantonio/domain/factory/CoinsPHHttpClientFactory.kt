package com.medina.juanantonio.domain.factory

import com.medina.juanantonio.domain.factory.plugins.CoinsPHAuthPlugin
import com.medina.juanantonio.domain.factory.plugins.GlobalRetryAfterStore
import com.medina.juanantonio.domain.factory.plugins.RetryAfterPlugin
import com.medina.juanantonio.domain.models.SettingsKeys.COINSPH
import com.medina.juanantonio.domain.models.network.CoinsPHAccount
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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

            install(RetryAfterPlugin) {
                store = GlobalRetryAfterStore(settings)
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
