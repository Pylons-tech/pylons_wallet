import kotlin.collections.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openjfx.gradle.JavaFXModule
import org.openjfx.gradle.JavaFXOptions
import org.openjfx.gradle.JavaFXPlatform

plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.beryx.runtime") version "1.12.1"
    application
}

group = "com.pylons"
version = "0.1"

val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
val jfxModules = listOf("javafx.base", "javafx.controls", "javafx.graphics", "javafx.fxml")
compileJava.destinationDir = compileKotlin.destinationDir

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

application {
    applicationName = "Pylons DevWallet"
    mainClassName = "com.pylons.devwallet.DevWalletApp"
}

javafx {
    modules = jfxModules
    configuration = "implementation"
}

val javaFXOptions = the<JavaFXOptions>()

dependencies {
    implementation(project(":walletcore"))

    implementation(kotlin("stdlib-jdk8"))

    implementation("no.tornado", "tornadofx", "1.7.17") {
        exclude("org.jetbrains.kotlin")
    }
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("com.google.protobuf:protobuf-java:3.11.4")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.5")
    implementation("com.beust:klaxon:5.0.12")

    testCompile("junit", "junit", "4.12")

    JavaFXPlatform.values().forEach {platform ->
        val cfg = configurations.create("javafx_" + platform.classifier)
        JavaFXModule.getJavaFXModules(javaFXOptions.modules).forEach { m ->
            project.dependencies.add(cfg.name,
                    String.format("org.openjfx:%s:%s:%s", m.artifactName, javaFXOptions.version, platform.classifier));
        }
    }
}


runtime {
    imageZip.set(project.file("${project.buildDir}/image-zip/hello-image.zip"))
    jpackage {
        skipInstaller = true
        imageName = "devwallet"
    }
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        sourceCompatibility = "1.8"
    }
}