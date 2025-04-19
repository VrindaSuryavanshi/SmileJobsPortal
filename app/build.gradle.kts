plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.smilejobportal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smilejobportal"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
        implementation ("com.google.firebase:firebase-auth-ktx")
        implementation ("com.google.firebase:firebase-database-ktx")

        // ViewModel dependency (required for viewModels() delegate)
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0") // or latest version
        // LiveData (optional, if you need LiveData support)
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
        // Activity extension for lifecycle-aware components
        implementation("androidx.activity:activity-ktx:1.3.1") // This is required for viewModels() in Activity



        implementation("com.github.bumptech.glide:glide:4.16.0")
//        kapt("com.github.bumptech.glide:compiler:4.16.0") // For annotation processing (e.g., GlideApp)




        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

        // Add the dependency for the Firebase Authentication library
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-auth")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}