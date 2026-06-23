package com.medina.juanantonio.data.auth

import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class OAuthCallbackServer {

    companion object {
        const val AUTH_CALLBACK_ENDPOINT = "/auth/strava/callback"
        const val PORT = 5173
    }

    val callbackServer by lazy {
        embeddedServer(CIO, port = PORT) {
            routing {
                get(AUTH_CALLBACK_ENDPOINT) {
                    val code = call.request.queryParameters["code"]

                    if (code != null) {
                        codeFlow.emit(code)
                    }

                    call.respondText(
                        "Login successful. You can close this window."
                    )

                    stop()
                }
            }
        }
    }

    private val codeFlow = MutableSharedFlow<String>()

    val codes: SharedFlow<String>
        get() = codeFlow.asSharedFlow()

    fun start() {
        callbackServer.start()
    }

    fun stop() {
        callbackServer.stop(gracePeriodMillis = 10000)
    }
}
