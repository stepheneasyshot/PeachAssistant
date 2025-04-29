package com.stephen.copywritinggene.di

import com.stephen.copywritinggene.network.NetworkRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<NetworkRepository> { NetworkRepository(get()) }
}