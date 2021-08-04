import kotlin.collections.*

plugins {
    java
    kotlin("jvm")
    `maven-publish`
    signing
}

group = "tech.pylons"
version = "0.1.0"
val ketheriumVer = "0.81.2"
val spongycastleVer = "1.58.0.0"
val junitVer = "5.6.0"
val useJava8 = project.hasProperty("java8")

configure<JavaPluginConvention> {
    val jVer = when (useJava8) {
        true -> JavaVersion.VERSION_1_8
        false -> JavaVersion.VERSION_15
    }
    sourceCompatibility = jVer
    targetCompatibility = jVer
}

configurations {
    all {
        //exclude("com.github.walleth.kethereum", "crypto_api")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":libpylons"))
    implementation(project(":walletcore"))

    implementation(kotlin("stdlib-jdk8"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")

    implementation("com.google.guava:guava:28.2-jre")
    implementation("commons-codec:commons-codec:1.14")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")
    implementation("com.madgag.spongycastle:prov:$spongycastleVer")
    implementation("com.madgag.spongycastle:bcpkix-jdk15on:$spongycastleVer")
    implementation("com.beust:klaxon:5.0.12")
    implementation("com.github.komputing:kbip44:0.1")
    implementation("com.github.walleth.kethereum:bip32:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39_wordlist_en:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_api:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_impl_spongycastle:$ketheriumVer")
    implementation("com.github.walleth.kethereum:model:$ketheriumVer")
    implementation("io.github.classgraph:classgraph:4.8.87")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVer")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVer")
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        val jVer = when (useJava8) {
            true -> "1.8"
            false -> "15"
        }
        jvmTarget = jVer
        sourceCompatibility = jVer
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("Pylons httpipc")
                description.set("Generic proof-of-concept implementation of Pylons IPC primitives using local HTTP")
                url.set("https://pylons.tech")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("Pylons-tech")
                        name.set("Pylons LLC")
                        email.set("info@pylons.tech")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Pylons-tech/pylons_wallet.git")
                    developerConnection.set("scm:git:ssh://github.com/Pylons-tech/pylons_wallet.git")
                    url.set("http://github.com/Pylons-tech/pylons_wallet/")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}