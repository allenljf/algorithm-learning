import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.tasks.Copy
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val wasmApiBaseUrl = providers.gradleProperty("apiBaseUrl")
    .orElse("http://localhost:8080")

val localReleaseSigning = Properties().also { properties ->
    val propertiesFile = rootProject.file("android-release-signing.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(properties::load)
    }
}

fun releaseSigningValue(key: String): String? =
    providers.gradleProperty("androidRelease${key.replaceFirstChar(Char::uppercase)}").orNull
        ?: localReleaseSigning.getProperty(key)

val releaseSigningKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val releaseSigningValues = releaseSigningKeys.associateWith(::releaseSigningValue)
val releaseSigningEnabled = releaseSigningValues.values.any { !it.isNullOrBlank() }

if (releaseSigningEnabled) {
    require(releaseSigningValues.values.all { !it.isNullOrBlank() }) {
        "Release signing requires storeFile, storePassword, keyAlias, and keyPassword."
    }
}

val androidVersionCode = providers.gradleProperty("androidVersionCode")
    .map(String::toInt)
    .getOrElse(1)
val androidVersionName = providers.gradleProperty("androidVersionName")
    .getOrElse("1.0")

tasks.withType<Copy>().configureEach {
    if (name == "wasmJsProcessResources") {
        inputs.property("apiBaseUrl", wasmApiBaseUrl)
        filesMatching("index.html") {
            expand("apiBaseUrl" to wasmApiBaseUrl.get())
        }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    listOf(iosArm64, iosSimulatorArm64).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        wasmJsTest.dependencies {
            implementation(libs.compose.uiTest)
        }
    }
}

android {
    namespace = "com.algorithmlearning"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.algorithmlearning"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = androidVersionCode
        versionName = androidVersionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            if (releaseSigningEnabled) {
                signingConfig = signingConfigs.maybeCreate("release").apply {
                    storeFile = file(releaseSigningValues.getValue("storeFile")!!)
                    storePassword = releaseSigningValues.getValue("storePassword")
                    keyAlias = releaseSigningValues.getValue("keyAlias")
                    keyPassword = releaseSigningValues.getValue("keyPassword")
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
