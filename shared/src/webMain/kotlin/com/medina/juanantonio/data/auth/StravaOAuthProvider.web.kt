package com.medina.juanantonio.data.auth

actual class StravaOAuthProvider actual constructor() {
    actual fun getCodes(): kotlinx.coroutines.flow.SharedFlow<String> {
        TODO("Not yet implemented")
    }

    actual fun authenticate() {
    }
}