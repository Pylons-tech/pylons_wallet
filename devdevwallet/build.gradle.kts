import kotlin.collections.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("org.beryx.runtime") version "1.8.0"
    application
}

group = "com.pylons"
version = "0.1a"

val bouncycastleVer = "1.64"
val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDir = compileKotlin.destinationDir

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

application {
    applicationName = "Pylons DevDevWallet"
    mainClassName = "com.pylons.devdevwallet.Main"
}

dependencies {
    implementation(project(":walletcore"))
    implementation(project(":httpipc"))

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.70")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.5")
    implementation("com.beust:klaxon:5.0.12")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncycastleVer")
    implementation("org.bouncycastle:bcpkix-jdk15on:$bouncycastleVer")

    testCompile("junit", "junit", "4.12")
}

runtime {

    imageZip.set(project.file("${project.buildDir}/image-zip/ddw-image.zip"))
    jpackage {
        jpackageHome = "C:\\Program Files\\Java\\jdk-14"
        skipInstaller = true
        imageName = "devdevwallet"
        mainClass = application.mainClassName
    }

    //additive.set(true)

    //modules.set(listOf("org.openjfx:javafx-base-12.0.1"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        sourceCompatibility = "1.8"
    }
}