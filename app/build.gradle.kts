import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "2.1.10-1.0.31"
    id("com.google.dagger.hilt.android") version "2.56.2"
}

android {
    namespace = "com.data.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.data.app"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "BASE_URL",
            gradleLocalProperties(rootDir, providers).getProperty("base.url")
        )

        buildConfigField(
            "String",
            "SEJONG_URL",
            gradleLocalProperties(rootDir, providers).getProperty("sejong.url")
        )
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
        buildConfig = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Button
    implementation("com.google.android.material:material:1.11.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.activity:activity-ktx:1.10.1")

    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.tbuonomo:dotsindicator:5.1.0")

    // flexbox
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    //hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")
    kapt("com.google.dagger:dagger-android-processor:2.46.1")

    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    //blurview
    implementation("com.github.Dimezis:BlurView:version-2.0.6")

    // lottifile
    implementation("com.airbnb.android:lottie:6.6.7")

    // shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // imageCrop
    implementation("com.github.yalantis:ucrop:2.2.6")
    // implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = false
}