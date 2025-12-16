plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat.v161)
    implementation(libs.material)
    implementation(libs.constraintlayout.v214)
    implementation(libs.swiperefreshlayout)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)
    implementation(libs.picasso)
    implementation(libs.cardview)

    implementation(libs.room.runtime)
    implementation(libs.play.services.location)
    annotationProcessor(libs.room.compiler)

    // implementation(libs.appcompat)
    // implementation(libs.material)
    // implementation(libs.activity)
    // implementation(libs.constraintlayout)

    implementation(libs.play.services.location)
    implementation(libs.dynamicanimation)
    implementation(libs.core)
    implementation(libs.play.services.maps)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)
}