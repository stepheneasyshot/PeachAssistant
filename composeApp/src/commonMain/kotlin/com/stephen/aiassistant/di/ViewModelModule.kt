package com.stephen.aiassistant.di

import com.stephen.aiassistant.helper.DataStoreHelper
import com.stephen.aiassistant.helper.PermissionHelper
import com.stephen.aiassistant.helper.ThemeHelper
import com.stephen.aiassistant.vm.MainViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get(), get(), get(), get()) }
    factory { ThemeHelper(get()) }
    factory { DataStoreHelper() }
    factory { PermissionHelper() }
}