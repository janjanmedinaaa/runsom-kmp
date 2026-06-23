package com.medina.juanantonio.di

import com.medina.juanantonio.presentation.ui.home.HomeViewModel
import com.medina.juanantonio.presentation.ui.home.modals.activity_form.SubmitActivityViewModel
import com.medina.juanantonio.presentation.ui.home.modals.contract_form.ContractFormViewModel
import com.medina.juanantonio.presentation.ui.home.modals.settings.SettingsFormViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory {
        HomeViewModel(
            stravaRepository = get(),
            coinsPHRepository = get(),
            contractRepository = get(),
            uiEventManager = get()
        )
    }

    factory {
        SettingsFormViewModel(
            coinsPHRepository = get(),
            uiEventManager = get()
        )
    }

    factory {
        ContractFormViewModel(
            contractRepository = get(),
            coinsPHRepository = get(),
            uiEventManager = get()
        )
    }

    factory {
        SubmitActivityViewModel(
            stravaRepository = get(),
            contractRepository = get(),
            coinsPHRepository = get(),
            uiEventManager = get()
        )
    }
}