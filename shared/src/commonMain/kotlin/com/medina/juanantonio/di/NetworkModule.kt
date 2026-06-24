package com.medina.juanantonio.di

import com.medina.juanantonio.data.network.CoinsPHRemoteSource
import com.medina.juanantonio.data.network.RunsomChallengesRemoteSource
import com.medina.juanantonio.data.network.StravaRemoteSource
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
        StravaRemoteSource(
            client = get(named("defaultClient"))
        )
    }

    single {
        CoinsPHRemoteSource(
            client = get(named("coinsPhHttpClient"))
        )
    }

    single {
        RunsomChallengesRemoteSource(
            client = get(named("defaultClient"))
        )
    }
}
