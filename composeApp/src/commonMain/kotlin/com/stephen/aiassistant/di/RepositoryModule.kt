package com.stephen.aiassistant.di

import com.stephen.aiassistant.network.NetworkRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<NetworkRepository> { NetworkRepository(get()) }
}