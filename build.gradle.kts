plugins {
    java
    kotlin("jvm") version "1.4.30"
    idea
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven(url = "https://jitpack.io")
    }
}

