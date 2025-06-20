plugins {
  id("com.android.application")
}

android {
  namespace = "dev.silvadev.blockode"
  compileSdk = 34

  defaultConfig {
    applicationId = "dev.silvadev.blockode"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    
    vectorDrawables.useSupportLibrary = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    
    isCoreLibraryDesugaringEnabled = true
  }

  signingConfigs {
    create("release") {
      // temporary keystore
      storeFile = file(layout.buildDirectory.dir("../release_key.jks"))
      storePassword = "release_temp"
      keyAlias = "release_temp"
      keyPassword = "release_temp"
    }
    getByName("debug") {
      storeFile = file(layout.buildDirectory.dir("../testkey.keystore"))
      storePassword = "testkey"
      keyAlias = "testkey"
      keyPassword = "testkey"
    }
  }
    
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      applicationIdSuffix = ".debug"
    }
  }

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
  
  implementation("androidx.constraintlayout:constraintlayout:2.2.0")
  implementation("com.google.android.material:material:1.13.0-alpha11")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.code.gson:gson:2.10.1")
  api("com.google.guava:guava:33.1.0-android")
  implementation("androidx.preference:preference:1.2.1")
  
  implementation("com.github.bumptech.glide:glide:4.16.0")
  implementation("net.lingala.zip4j:zip4j:2.11.5")
  implementation("com.github.MindorksOpenSource:PRDownloader:0.6.0")
  
  val editorVersion = "0.23.4"
  implementation("io.github.Rosemoe.sora-editor:editor:$editorVersion")
  implementation("io.github.Rosemoe.sora-editor:language-textmate:$editorVersion")
  implementation("io.github.Rosemoe.sora-editor:language-treesitter:$editorVersion")
  implementation("com.itsaky.androidide.treesitter:android-tree-sitter:4.3.1")
  implementation("com.itsaky.androidide.treesitter:tree-sitter-java:4.3.1")
  
  implementation(project(":file-tree"))
  implementation(project(":build-tools"))
  //implementation(project(":feature:java-completion"))
  implementation(project(":javac"))
  
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}