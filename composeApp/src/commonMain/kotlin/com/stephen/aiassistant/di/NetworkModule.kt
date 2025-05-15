package com.stephen.aiassistant.di

import com.stephen.aiassistant.network.KtorClient
import org.koin.dsl.module

val httpClientModule = module {
    single<KtorClient> {
        KtorClient()
    }
}