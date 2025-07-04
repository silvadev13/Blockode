plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api("com.github.marschall:zipfilesystem-standalone:1.0.1")
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}
