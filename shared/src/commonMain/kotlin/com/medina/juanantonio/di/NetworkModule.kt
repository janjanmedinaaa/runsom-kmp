package com.medina.juanantonio.di

import com.medina.juanantonio.data.network.CoinsPHAPIService
import com.medina.juanantonio.data.network.StravaAPIService
import com.medina.juanantonio.domain.factory.CoinsPHHttpClientFactory
import com.medina.juanantonio.domain.factory.HttpClientFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {

    single(named("defaultClient")) {
        HttpClientFactory.create()
    }

    single(named("coinsPhHttpClient")) {
        CoinsPHHttpClientFactory(
            settings = get()
        ).create()
    }

    single {
        StravaAPIService(
            client = get(named("defaultClient"))
        )
    }

    single {
        CoinsPHAPIService(
            client = get(named("coinsPhHttpClient"))
        )
    }
}
