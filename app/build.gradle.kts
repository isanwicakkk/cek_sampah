plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cek_sampah"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cek_sampah"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    packaging {
        resources {
            pickFirsts.add("lib/armeabi-v7a/libtensorflowlite_jni.so")
            pickFirsts.add("lib/arm64-v8a/libtensorflowlite_jni.so")
            pickFirsts.add("lib/armeabi-v7a/libtensorflowlite_flex_jni.so")
            pickFirsts.add("lib/arm64-v8a/libtensorflowlite_flex_jni.so")
            pickFirsts.add("lib/x86/libtensorflowlite_jni.so")
            pickFirsts.add("lib/x86/libtensorflowlite_flex_jni.so")
            pickFirsts.add("lib/arm64-v8a/libtensorflowlite_jni.so")
            pickFirsts.add("lib/x86_64/libtensorflowlite_jni.so")
            pickFirsts.add("lib/x86_64/libtensorflowlite_flex_jni.so")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        getByName("main") {
            assets {
            }
        }
    }
    aaptOptions {
        noCompress += "tflite"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.camera:camera-camera2:1.5.3")
    implementation("androidx.camera:camera-lifecycle:1.5.3")
    implementation("androidx.camera:camera-view:1.5.3")
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
}



