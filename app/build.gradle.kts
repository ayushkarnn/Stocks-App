plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs")
    id("kotlin-kapt")
}

android {
    namespace = "ayush.karn.stocksapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "ayush.karn.stocksapp"
        minSdk = 24
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
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.smoothbottombar)

    //for navgraph and navigation component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)



    // scalable size unit by intuit(Support For Different screen size)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)


    //Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)

    //Live Data
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //View Model
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //RETROFIT
    implementation(libs.retrofit)

    //GSON CONVERTER
    implementation(libs.converter.gson)

    implementation(libs.circleimageview)
    implementation(libs.roundedimageview)
    implementation(libs.neumorphism)

}