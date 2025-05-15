package com.stephen.aiassistant.di

import com.stephen.aiassistant.network.DeepseekChatRepository
import com.stephen.aiassistant.network.DoubaoVisionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<DeepseekChatRepository> { DeepseekChatRepository(get()) }
    single<DoubaoVisionRepository> { DoubaoVisionRepository(get()) }
}