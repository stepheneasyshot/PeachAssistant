import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kmpAppIconGeneratorPlugin)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(project(":permissions"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.compose.jb)
            implementation(libs.navigation.compose)
            implementation(libs.bundles.ktor)
            implementation(libs.ktor.kotlin.serialization)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.aakira.napier)
            implementation(libs.compottie)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            // 选取图片
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.coil)
            // coil official
            implementation(libs.coil.compose)
            // camera
            implementation(libs.camerak)
            implementation(libs.datastore)
            implementation(libs.datastore.preferences)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(compose.material3)
        }
    }
}

android {
    namespace = "com.stephen.aiassistant"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.stephen.aiassistant"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        val aaosSign = "stephen"
        register("aaos") {
            keyAlias = aaosSign
            keyPassword = aaosSign
            storeFile = file("./keystores/platform.jks")
            storePassword = aaosSign
        }
    }
    buildTypes {
        buildTypes {
            getByName("release") {
                signingConfig = signingConfigs.getByName("aaos")
                isMinifyEnabled = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    val appName = "PeachAssistant"
    android.applicationVariants.configureEach {
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                this.outputFileName = "${appName}_V${defaultConfig.versionName}.apk"
            }
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

