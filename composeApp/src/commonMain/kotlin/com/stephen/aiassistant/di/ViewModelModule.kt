package com.stephen.aiassistant.di

import com.stephen.aiassistant.vm.MainViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get()) }
}