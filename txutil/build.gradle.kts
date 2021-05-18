plugins {
    java
    kotlin("jvm")
}

group = "tech.pylons"
version = "1.0-SNAPSHOT"

//sourceCompatibility = 1.8

repositories {
    mavenCentral()
    jcenter()
    google()
}

dependencies {
    implementation(project(":libkylons"))
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