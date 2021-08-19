/*
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("tech.pylons:recipetool:0.1.4")
    }
}
*/

plugins {
    java
    kotlin("jvm")
//    id("tech.pylons.recipetool") version "0.1.4"
}

group = "tech.pylons"
version = "0.1.0"

//sourceCompatibility = 1.8

dependencies {
    implementation(project(":libpylons"))
    implementation(project(":walletcore"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.beust:klaxon:4.0.2")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")

    testImplementation("junit:junit:4.12")
}

tasks.compileKotlin.get().destinationDir = tasks.compileJava.get().destinationDir

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}