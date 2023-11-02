    plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.nurtore.imam_ai"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.nurtore.imam_ai"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        //for future migrations
        kapt{
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.3")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


//    constraints {
//        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
//            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
//        }
//        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
//            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
//        }
//    }


    // view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //retorfit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//  implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    //room db
    //implementation("androidx.room:room-runtime:2.5.2")
    //annotationProcessor("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    //ksp is faster, for future updates
    kapt("androidx.room:room-compiler:2.5.2")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1") // Use the latest version

    // For notifications
    implementation("androidx.core:core-ktx:1.7.0") // Use the latest version

    // location
    implementation("com.google.android.gms:play-services-location:21.0.1")

}