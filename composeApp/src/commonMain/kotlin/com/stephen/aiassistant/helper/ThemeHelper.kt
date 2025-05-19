package com.stephen.aiassistant.helper

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.stephen.aiassistant.data.ThemeState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeHelper(private val dataStoreHelper: DataStoreHelper) {

    private val _themeState = MutableStateFlow(ThemeState.SYSTEM)
    val themeStateStateFlow = _themeState.asStateFlow()
    private val themePreferencesKey = stringPreferencesKey("ThemeState")

    /**
     * 下发主题切换，存储在dataStore中
     */
    fun setThemeState(themeState: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                dataStoreHelper.dataStore.edit {
                    it[themePreferencesKey] = themeState.toString()
                }
                _themeState.update {
                    themeState
                }
            }.onFailure {
                Napier.e("setThemeState-> error:${it.message}")
            }
        }
    }

    /**
     * 获取本地存储的主题
     */
    fun updateThemeState() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreHelper.dataStore.data.collect {
                val themeState = it[themePreferencesKey]?.toInt() ?: ThemeState.DARK
                Napier.i("getThemeState-> themeState:$themeState")
                _themeState.update {
                    themeState
                }
            }
        }
    }
}