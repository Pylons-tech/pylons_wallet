plugins {
    java
    kotlin("jvm") version "1.3.70"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven(url = "https://jitpack.io")
    }
}

