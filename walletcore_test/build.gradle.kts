plugins {
    java
    kotlin("jvm")
    idea
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

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":walletcore"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")

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

    implementation("org.junit.jupiter:junit-jupiter-api:$junitVer")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVer")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.compileJava {
    dependsOn(":compileKotlin")
    doFirst {
        classpath = files()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    doFirst {
        destinationDir = tasks.compileTestJava.get().destinationDir
    }

    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}