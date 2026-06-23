package com.medina.juanantonio.di

import com.medina.juanantonio.data.local.ContractLocalDataSource
import com.medina.juanantonio.data.local.ContractStravaActivityLocalDataSource
import com.medina.juanantonio.data.repository.CoinsPHRepository
import com.medina.juanantonio.data.repository.ContractRepository
import com.medina.juanantonio.data.repository.StravaRepository
import org.koin.dsl.module

val repositoryModule = module {
    single {
        StravaRepository(
            oAuthProvider = get(),
            apiService = get(),
            settings = get()
        )
    }

    single {
        CoinsPHRepository(
            settings = get(),
            apiService = get()
        )
    }

    single {
        ContractLocalDataSource(
            database = get()
        )
    }

    single {
        ContractStravaActivityLocalDataSource(
            database = get()
        )
    }

    single {
        ContractRepository(
            contractLocalDataSource = get(),
            activityLocalDataSource = get()
        )
    }
}
