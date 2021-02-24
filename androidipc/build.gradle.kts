import java.util.Properties

val local = Properties()
val localProperties: File = rootProject.file("local.properties")
if (localProperties.exists()) {
    localProperties.inputStream().use { local.load(it) }
}

val androidSdk: String? = local.getProperty("sdk.dir")

if (androidSdk.isNullOrEmpty()) println("WARNING: No sdk.dir specified in local.properties")
else println("Android sdk at $androidSdk")


plugins {
    idea
    java
    kotlin("jvm")
    //id("com.android.library")
}

group = "com.pylons"
version = "0.1a"
val ketheriumVer = "0.81.2"
val bouncycastleVer = "1.64"
val junitVer = "5.6.0"

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(files("$androidSdk/platforms/android-28/android.jar"))
    implementation(project(":walletcore"))

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
    implementation("com.github.walleth.kethereum:crypto_api:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_impl_bouncycastle:$ketheriumVer")
    implementation("com.github.walleth.kethereum:model:$ketheriumVer")
    implementation("io.github.classgraph:classgraph:4.8.87")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVer")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVer")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        sourceCompatibility = "1.8"
    }
}