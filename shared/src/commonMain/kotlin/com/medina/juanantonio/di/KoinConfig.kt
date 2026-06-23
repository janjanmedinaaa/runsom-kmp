package com.medina.juanantonio.di

import org.koin.dsl.koinConfiguration

fun createKoinConfig() = koinConfiguration {
    modules(
        repositoryModule,
        viewModelModule,
        providerModule,
        networkModule
    )
}