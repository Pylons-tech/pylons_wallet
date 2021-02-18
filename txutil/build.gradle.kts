import kotlin.collections.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("org.beryx.runtime") version "1.8.0"
    application
}

group = "com.pylons"
version = "1.0-SNAPSHOT"

//sourceCompatibility = 1.8

repositories {
    mavenCentral()
    jcenter()
    google()
}

application {
    applicationName = "txutil"
    mainClassName = "com.pylons.txutil.Main"
}


dependencies {
    implementation(project(":walletcore"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.beust:klaxon:4.0.2")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")

    testImplementation("junit:junit:4.12")
}

runtime {

    imageZip.set(project.file("${project.buildDir}/image-zip/tu-image.zip"))
    jpackage {
        jpackageHome = "C:\\Program Files\\Java\\jdk-14"
        skipInstaller = true
        imageName = "txutil"
        mainClass = application.mainClassName
    }

    //additive.set(true)

    //modules.set(listOf("org.openjfx:javafx-base-12.0.1"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

}


tasks.compileKotlin.get().destinationDir = tasks.compileJava.get().destinationDir

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}