plugins {
    id("com.android.library")
}
android {
    namespace = "dev.silvadev.build"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":javac"))
    implementation("com.android.tools:r8:8.5.35")
    implementation("com.google.code.gson:gson:2.10.1")
    api("com.google.guava:guava:33.1.0-android")
}
