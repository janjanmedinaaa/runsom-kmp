package com.medina.juanantonio.data.auth

import kotlinx.coroutines.flow.SharedFlow

actual class StravaOAuthProvider actual constructor() {
    actual fun getCodes(): SharedFlow<String> {
        TODO("Not yet implemented")
    }

    actual fun authenticate() {
    }
}