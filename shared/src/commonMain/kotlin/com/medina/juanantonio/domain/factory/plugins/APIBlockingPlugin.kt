package com.medina.juanantonio.domain.factory.plugins

import com.russhwolf.settings.Settings
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import kotlin.time.Clock

class ApiBlockedException : RuntimeException(
    "API temporarily blocked due to Retry-After"
)

class GlobalRetryAfterStore(
    private val settings: Settings
) {
    private val key = "global_retry_after_blocked_until"

    fun isBlocked(): Boolean {
        val blockedUntil = settings.getLongOrNull(key) ?: return false
        val now = Clock.System.now().toEpochMilliseconds()
        return now < blockedUntil
    }

    fun block(retryAfterSeconds: Long) {
        val now = Clock.System.now().toEpochMilliseconds()
        val blockedUntil = now + retryAfterSeconds * 1000
        settings.putLong(key, blockedUntil)
    }
}

class RetryAfterPluginConfig {
    lateinit var store: GlobalRetryAfterStore

    var blockedStatuses = setOf(
        HttpStatusCode.TooManyRequests,
        HttpStatusCode(418, "IP-Banned")
    )
}

val RetryAfterPlugin = createClientPlugin(
    name = "RetryAfterPlugin",
    createConfiguration = ::RetryAfterPluginConfig
) {
    val store = pluginConfig.store

    onRequest { _, _ ->
        if (store.isBlocked()) {
            throw ApiBlockedException()
        }
    }

    onResponse { response ->
        val status = response.status

        if (!this@createClientPlugin.pluginConfig.blockedStatuses.contains(status)) {
            return@onResponse
        }

        val retryAfterHeader = response.headers["Retry-After"]
        val retryAfterSeconds = retryAfterHeader?.toLongOrNull() ?: 120

        store.block(retryAfterSeconds)
    }
}
