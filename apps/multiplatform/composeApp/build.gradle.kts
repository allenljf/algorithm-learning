import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

compose.resources {
    packageOfResClass = "com.algorithmlearning.app.generated.resources"
    generateResClass = always
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

val verifyWasmViewport = tasks.register("verifyWasmViewport") {
    group = "verification"
    description = "Verifies that the Wasm page host fills the browser viewport."
    val indexHtml = layout.projectDirectory.file("src/wasmJsMain/resources/index.html")
    inputs.file(indexHtml)
    doLast {
        val document = indexHtml.asFile.readText()
        check(
            "html, body, #composeTarget" in document &&
                "height: 100%;" in document &&
                "ResizeObserver" in document &&
                "window.dispatchEvent(new Event(\"resize\"))" in document,
        ) {
            "The Wasm entry document must give Compose a full-height host and notify it when that host changes size."
        }
    }
}

val verifyWasmNativeAuthInputs = tasks.register("verifyWasmNativeAuthInputs") {
    group = "verification"
    description = "Verifies that Wasm authentication uses native browser inputs."
    val nativeFields = layout.projectDirectory.file(
        "src/wasmJsMain/kotlin/com/algorithmlearning/app/auth/PlatformAuthFields.wasmJs.kt",
    )
    inputs.file(nativeFields)
    doLast {
        val source = nativeFields.asFile.readText()
        val requiredNativeInputContract = listOf(
            "native-auth-input-contract",
            "document.createElement(\"input\")",
            "kind = \"email\"",
            "kind = \"password\"",
            "addEventListener(\"input\"",
            "composePixelsToCssPixels",
            "input.style.left = cssPixels(position.x)",
            "input.style.width = cssPixels(coordinates.size.width.toFloat())",
            "style.left = cssPixels(position.x + coordinates.size.width - 12f)",
        )
        val missing = requiredNativeInputContract.filterNot(source::contains)
        check(missing.isEmpty()) {
            "The Wasm authentication form must retain its native-input contract; missing: $missing"
        }
    }
}

val verifyWasmCjkFont = tasks.register("verifyWasmCjkFont") {
    group = "verification"
    description = "Verifies that Web/Wasm ships a Traditional Chinese font."
    val fontFile = layout.projectDirectory.file(
        "src/commonMain/composeResources/font/NotoSansTC-Regular.otf",
    )
    val theme = layout.projectDirectory.file(
        "src/commonMain/kotlin/com/algorithmlearning/app/Theme.kt",
    )
    inputs.files(fontFile, theme)
    doLast {
        check(fontFile.asFile.isFile) {
            "The Web/Wasm application must bundle a Traditional Chinese font."
        }
        check(theme.asFile.readText().contains("Res.font.NotoSansTC_Regular")) {
            "The application theme must use the bundled Traditional Chinese font."
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
            implementation(libs.compose.components.resources)
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

val wasmJsHostingDirectory = layout.buildDirectory.dir("firebaseHosting")
val prepareWasmJsFirebaseHosting = tasks.register<Sync>("prepareWasmJsFirebaseHosting") {
    dependsOn("wasmJsBrowserProductionWebpack")
    from(layout.buildDirectory.dir("kotlin-webpack/wasmJs/productionExecutable"))
    from(layout.buildDirectory.dir("processedResources/wasmJs/main"))
    into(wasmJsHostingDirectory)
}

tasks.named("wasmJsBrowserProductionWebpack") {
    finalizedBy(prepareWasmJsFirebaseHosting)
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
