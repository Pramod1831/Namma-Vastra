import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val geminiApiKey = (project.findProperty("GEMINI_API_KEY") as String?)
    ?: localProps.getProperty("GEMINI_API_KEY")
    ?: ""
val supabaseUrl = (project.findProperty("SUPABASE_URL") as String?)
    ?: localProps.getProperty("SUPABASE_URL")
    ?: ""
val supabasePublishableKey = (project.findProperty("SUPABASE_PUBLISHABLE_KEY") as String?)
    ?: localProps.getProperty("SUPABASE_PUBLISHABLE_KEY")
    ?: ""
val googleWebClientId = (project.findProperty("GOOGLE_WEB_CLIENT_ID") as String?)
    ?: localProps.getProperty("GOOGLE_WEB_CLIENT_ID")
    ?: ""
val firebaseProjectId = (project.findProperty("FIREBASE_PROJECT_ID") as String?)
    ?: localProps.getProperty("FIREBASE_PROJECT_ID")
    ?: ""
val firebaseApplicationId = (project.findProperty("FIREBASE_APPLICATION_ID") as String?)
    ?: localProps.getProperty("FIREBASE_APPLICATION_ID")
    ?: ""
val firebaseApiKey = (project.findProperty("FIREBASE_API_KEY") as String?)
    ?: localProps.getProperty("FIREBASE_API_KEY")
    ?: ""
val firebaseStorageBucket = (project.findProperty("FIREBASE_STORAGE_BUCKET") as String?)
    ?: localProps.getProperty("FIREBASE_STORAGE_BUCKET")
    ?: ""
val firebaseSenderId = (project.findProperty("FIREBASE_SENDER_ID") as String?)
    ?: localProps.getProperty("FIREBASE_SENDER_ID")
    ?: ""
val adminEmail = (project.findProperty("ADMIN_EMAIL") as String?)
    ?: localProps.getProperty("ADMIN_EMAIL")
    ?: ""

android {
    namespace = "com.nammavastra"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nammavastra.com"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$geminiApiKey\""
        )
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"$supabaseUrl\""
        )
        buildConfigField(
            "String",
            "SUPABASE_PUBLISHABLE_KEY",
            "\"$supabasePublishableKey\""
        )
        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"$googleWebClientId\""
        )
        buildConfigField(
            "String",
            "FIREBASE_PROJECT_ID",
            "\"$firebaseProjectId\""
        )
        buildConfigField(
            "String",
            "FIREBASE_APPLICATION_ID",
            "\"$firebaseApplicationId\""
        )
        buildConfigField(
            "String",
            "FIREBASE_API_KEY",
            "\"$firebaseApiKey\""
        )
        buildConfigField(
            "String",
            "FIREBASE_STORAGE_BUCKET",
            "\"$firebaseStorageBucket\""
        )
        buildConfigField(
            "String",
            "FIREBASE_SENDER_ID",
            "\"$firebaseSenderId\""
        )
        buildConfigField(
            "String",
            "ADMIN_EMAIL",
            "\"$adminEmail\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

configurations.all {
    resolutionStrategy {
        force(
            "androidx.core:core:1.13.1",
            "androidx.core:core-ktx:1.13.1",
            "androidx.browser:browser:1.8.0",
            "org.jetbrains.kotlin:kotlin-stdlib:1.9.24",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24",
            "org.jetbrains.kotlin:kotlin-reflect:1.9.24",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0",
            "androidx.lifecycle:lifecycle-runtime-compose:2.7.0",
            "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0",
            "androidx.compose.ui:ui:1.6.7",
            "androidx.compose.ui:ui-android:1.6.7",
            "androidx.compose.ui:ui-graphics:1.6.7",
            "androidx.compose.ui:ui-graphics-android:1.6.7",
            "androidx.compose.ui:ui-text:1.6.7",
            "androidx.compose.ui:ui-text-android:1.6.7",
            "androidx.compose.ui:ui-text-google-fonts:1.6.7",
            "androidx.compose.ui:ui-tooling:1.6.7",
            "androidx.compose.ui:ui-tooling-android:1.6.7",
            "androidx.compose.ui:ui-tooling-data:1.6.7",
            "androidx.compose.ui:ui-tooling-data-android:1.6.7",
            "androidx.compose.foundation:foundation:1.6.7",
            "androidx.compose.foundation:foundation-android:1.6.7",
            "androidx.compose.foundation:foundation-layout:1.6.7",
            "androidx.compose.foundation:foundation-layout-android:1.6.7",
            "androidx.compose.animation:animation:1.6.7",
            "androidx.compose.animation:animation-android:1.6.7",
            "androidx.compose.animation:animation-core:1.6.7",
            "androidx.compose.animation:animation-core-android:1.6.7",
            "androidx.compose.runtime:runtime:1.6.7",
            "androidx.compose.runtime:runtime-android:1.6.7",
            "androidx.compose.runtime:runtime-saveable:1.6.7",
            "androidx.compose.runtime:runtime-saveable-android:1.6.7",
            "io.ktor:ktor-client-core:2.3.9",
            "io.ktor:ktor-client-core-jvm:2.3.9",
            "io.ktor:ktor-client-content-negotiation:2.3.9",
            "io.ktor:ktor-client-content-negotiation-jvm:2.3.9",
            "io.ktor:ktor-http:2.3.9",
            "io.ktor:ktor-http-jvm:2.3.9",
            "io.ktor:ktor-http-cio:2.3.9",
            "io.ktor:ktor-http-cio-jvm:2.3.9",
            "io.ktor:ktor-io:2.3.9",
            "io.ktor:ktor-io-jvm:2.3.9",
            "io.ktor:ktor-utils:2.3.9",
            "io.ktor:ktor-utils-jvm:2.3.9",
            "io.ktor:ktor-events:2.3.9",
            "io.ktor:ktor-events-jvm:2.3.9",
            "io.ktor:ktor-serialization:2.3.9",
            "io.ktor:ktor-serialization-jvm:2.3.9",
            "io.ktor:ktor-serialization-kotlinx-json:2.3.9",
            "io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.9",
            "org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3",
            "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.3",
            "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3",
            "org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3"
        )
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.ui:ui-graphics:1.6.7")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")
    implementation("androidx.compose.foundation:foundation:1.6.7")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.7")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("io.ktor:ktor-client-android:2.3.9")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.9")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
    implementation("io.ktor:ktor-client-logging:2.3.9")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
