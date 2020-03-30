import kotlin.collections.*

plugins {
    java
    kotlin("jvm")
}

group = "com.pylons"
version = "0.1a"
val ketheriumVer = "0.81.2"
val bouncycastleVer = "1.64"
val junitVer = "5.6.0"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configurations {
    all {
        exclude("com.github.walleth.kethereum", "crypto_api")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")

    implementation("com.google.guava:guava:28.2-jre")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncycastleVer")
    implementation("org.bouncycastle:bcpkix-jdk15on:$bouncycastleVer")
    implementation("com.beust:klaxon:5.0.12")
    implementation("com.github.komputing:kbip44:0.1")
    implementation("com.github.walleth.kethereum:bip32:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39_wordlist_en:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_impl_bouncycastle:$ketheriumVer")
    implementation("com.github.walleth.kethereum:model:$ketheriumVer")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Class-Path"] = configurations.compile.get().joinToString { "$name " }
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

tasks.compileJava {
    //dependsOn(":compileKotlin")
    doFirst {
        //options.compilerArgs = listOf("--module-path", classpath.asPath)
        //classpath = files()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    doFirst {
        //destinationDir = tasks.compileJava.get().destinationDir
    }

    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}