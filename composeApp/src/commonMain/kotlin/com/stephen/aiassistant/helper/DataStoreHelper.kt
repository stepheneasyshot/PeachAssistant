package com.stephen.aiassistant.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.stephen.aiassistant.platform.createDataStoreMultiplatform
import okio.Path.Companion.toPath

class DataStoreHelper {

    var dataStore: DataStore<Preferences> = createDataStoreMultiplatform()

}

const val DATASTORE_FILE_NAME = "ai_assistant_ds.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )