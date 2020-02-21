plugins {
    java
    kotlin("jvm") version ("1.3.61")
}

group = "com.pylons"
version = "1.0-SNAPSHOT"

//sourceCompatibility = 1.8

repositories {
    mavenCentral()
    jcenter()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.64")
    implementation("com.beust:klaxon:4.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.6.0")
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