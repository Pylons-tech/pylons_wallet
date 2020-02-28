plugins {
    java
    kotlin("jvm") version ("1.3.61")
}

group = "com.pylons"
version = "1.0-SNAPSHOT"
val ketheriumVer = "0.81.2"
val bouncycastleVer = "1.64"
val junitVer = "5.6.0"
//sourceCompatibility = 1.8

repositories {
    mavenCentral()
    jcenter()
    google()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncycastleVer")
    implementation("org.bouncycastle:bcpkix-jdk15on:$bouncycastleVer")
    implementation("com.beust:klaxon:4.0.2")
    implementation("com.github.komputing:kbip44:0.1")
    implementation("com.github.walleth.kethereum:bip32:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39_wordlist_en:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_impl_bouncycastle:$ketheriumVer")
    implementation("com.github.walleth.kethereum:model:$ketheriumVer")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVer")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVer")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        sourceCompatibility = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}