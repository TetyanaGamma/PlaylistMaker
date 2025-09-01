
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // --- Базовые Android библиотеки ---
    implementation(libs.androidx.core.ktx)           // Core KTX – расширения для стандартных классов Android
    implementation(libs.androidx.appcompat)          // AppCompat – поддержка старых версий Android
    implementation(libs.androidx.activity)           // Activity KTX – удобные расширения для Activity
    implementation(libs.androidx.constraintlayout)   // ConstraintLayout – современный лайаут для гибкой вёрстки
    implementation(libs.androidx.fragment.ktx)       // Fragment KTX – удобные расширения для работы с фрагментами

    // --- Material Design ---
    implementation(libs.material)                    // Material Components – UI-компоненты по гайдлайнам Material 3

    // --- Загрузка изображений ---
    implementation(libs.glide)                       // Glide – загрузка и кэширование изображений
    annotationProcessor(libs.glide.compiler)         // Аннотационный процессор для генерации кода Glide

    // --- Сериализация / JSON ---
    implementation(libs.gson)                        // Gson – сериализация и десериализация JSON

    // --- Сетевое взаимодействие ---
    implementation(libs.retrofit)                    // Retrofit – HTTP клиент
    implementation(libs.converter.gson)              // Конвертер JSON → Kotlin объектов для Retrofit

    // --- DI (Dependency Injection) ---
    implementation(libs.koin.android)                // Koin – внедрение зависимостей

    // --- Навигация ---
    implementation(libs.navigation.fragment.ktx)     // Навигация через фрагменты
    implementation(libs.navigation.ui.ktx)           // Навигация + UI (BottomNavigationView, Toolbar и т.д.)

    // --- Тестирование ---
    testImplementation(libs.junit)                   // JUnit – модульные тесты
    androidTestImplementation(libs.androidx.junit)   // AndroidJUnit – инструментальные тесты
    androidTestImplementation(libs.espresso.core)    // Espresso – UI-тесты
}
