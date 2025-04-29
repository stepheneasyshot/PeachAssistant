package com.stephen.copywritinggene.di

import com.stephen.copywritinggene.vm.MainViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get()) }
}