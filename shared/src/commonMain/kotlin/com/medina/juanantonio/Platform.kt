package com.medina.juanantonio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform