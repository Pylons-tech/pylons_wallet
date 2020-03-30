import kotlin.collections.*

plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.runtime") version "1.8.0"
    application
}

group = "com.pylons"
version = "0.1a"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":walletcore"))

    implementation(kotlin("stdlib-jdk8"))

    implementation("no.tornado", "tornadofx", "1.7.17")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("com.google.protobuf:protobuf-java:3.11.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.70")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.5")

    testCompile("junit", "junit", "4.12")
}

javafx {
    //modules("javafx.base", "javafx.controls", "javafx.graphics", "javafx.fxml")
    configuration = "implementation"
}

runtime {
}

//
//jlink {
//    addExtraDependencies("javafx")
//    options.add("--ignore-signing-information")
//    launcher {
//        name = "Pylons DevWallet"
//
//    }
//
//    jpackage {
//        jpackageHome = "C:\\Program Files\\Java\\jdk-14"
//        skipInstaller = true
//        imageName = "Pylons DevWallet"
//
//    }
//
//    mergedModule {
//        requires ("javafx.graphics")
//        requires ("javafx.controls")
//        requires ("java.json")
//        requires ("java.logging")
//        requires ("java.prefs")
//        requires ("java.desktop")
//        requires ("java.compiler")
//        requires ("javafx.base")
//        requires ("javafx.fxml")
//        requires ("jdk.unsupported")
//        uses ("tornadofx.Stylesheet")
//        uses ("tornadofx.ChildInterceptor")
//        provides ("kotlin.reflect.jvm.internal.impl.resolve.ExternalOverridabilityCondition").with( "kotlin.reflect.jvm.internal.impl.load.java.ErasedOverridabilityCondition",
//                "kotlin.reflect.jvm.internal.impl.load.java.FieldOverridabilityCondition",
//                "kotlin.reflect.jvm.internal.impl.load.java.JavaIncompatibilityRulesOverridabilityCondition")
//        provides ("kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoader").with("kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.BuiltInsLoaderImpl")
//    }
//}

application {
    applicationName = "Pylons DevWallet"
    mainClassName = "com.pylons.devwallet.DevWalletApp"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Class-Path"] = configurations.compile.get().joinToString { "$name " }
        attributes["Main-Class"] = application.mainClassName
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    for (it in configurations.runtimeClasspath.get().files) {
        var f = it
        if (f.isDirectory) f = zipTree(f).singleFile
        exclude("META-INF/MANIFEST.MF")
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    doFirst {
        destinationDir = tasks.compileJava.get().destinationDir
    }

    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}