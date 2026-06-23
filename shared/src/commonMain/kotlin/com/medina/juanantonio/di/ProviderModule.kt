package com.medina.juanantonio.di

import com.medina.juanantonio.data.auth.StravaOAuthProvider
import com.medina.juanantonio.data.manager.UIEventManager
import com.medina.juanantonio.domain.database.DatabaseFactory
import com.medina.juanantonio.domain.database.getRoomDatabase
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val providerModule = module {
    single {
        StravaOAuthProvider()
    }

    single {
        Settings()
    }

    single {
        getRoomDatabase(DatabaseFactory.create())
    }

    single {
        UIEventManager()
    }
}
