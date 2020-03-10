plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version "0.0.8"
    application
}

group = "com.pylons"
version = "0.1a"


dependencies {
    implementation("no.tornado", "tornadofx", "1.7.17")
    testCompile("junit", "junit", "4.12")
    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

javafx {
    modules("javafx.base", "javafx.controls", "javafx.fxml")
}

application {
    mainClassName = "com.pylons.devwallet.DevWalletApp"
}

dependencies {
    implementation(project(":walletcore"))
    implementation("org.apache.commons:commons-lang3:3.9")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Class-Path"] = configurations.compile.get().joinToString { "$name " }
        attributes["Main-Class"] = application.mainClassName
    }

     for (it in configurations.runtimeClasspath.get().files) {
         var f = it
         if (f.isDirectory) f = zipTree(f).singleFile
         exclude("META-INF/MANIFEST.MF")
         exclude("META-INF/*.SF")
         exclude("META-INF/*.DSA")
         exclude("META-INF/*.RSA")
     }
}