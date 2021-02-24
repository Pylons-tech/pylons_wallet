

plugins {
    kotlin("jvm") version("1.4.30")
    idea
    java
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven(url = "https://jitpack.io")
    }
}

