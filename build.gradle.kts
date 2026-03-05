plugins {
    id("java")
}

group = "me.nullicorn"
version = "1.0.0-SNAPSHOT"

val serverVersion = "2026.03.05-9fdc5985d"

repositories {
    mavenCentral()
    maven {
        name = "hytale-pre-release"
        url = uri("https://maven.hytale.com/pre-release/")
    }
    maven {
        name = "hytale-release"
        url = uri("https://maven.hytale.com/release/")
    }
}

dependencies {
    implementation("com.hypixel.hytale:Server:$serverVersion")
}