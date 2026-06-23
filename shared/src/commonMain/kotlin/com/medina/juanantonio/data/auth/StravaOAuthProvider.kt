package com.medina.juanantonio.data.auth

import kotlinx.coroutines.flow.SharedFlow

expect class StravaOAuthProvider() {

    fun getCodes(): SharedFlow<String>

    fun authenticate()
}
