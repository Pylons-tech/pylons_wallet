plugins {
    java
    kotlin("jvm") version "1.3.70"
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

