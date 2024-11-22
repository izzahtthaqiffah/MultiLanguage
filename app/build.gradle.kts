plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.multilanguange"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.multilanguange"
        minSdk = 31
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

    implementation("com.google.mlkit:common:18.11.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.activity)
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-database:20.0.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("androidx.media:media:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("com.google.guava:guava:33.3.1-android")
    implementation("org.apache.commons:commons-collections4:4.4")

    // ML Kit Translate library
    implementation("com.google.mlkit:translate:17.0.3")

    // ML Kit Text Recognition library (standalone)
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:16.0.0")

    // Other required libraries
    implementation("androidx.multidex:multidex:2.0.1")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
}
