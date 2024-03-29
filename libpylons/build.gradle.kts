import kotlin.collections.*

plugins {
    java
    kotlin("jvm")
    `maven-publish`
    signing
}

group = "tech.pylons"
version = "0.1.2"
val ketheriumVer = "0.83.4"
val spongyCastleVer = "1.58.0.0"
val junitVer = "5.6.0"
val useJava8 = true
//val usejava8 = false

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

    implementation(kotlin("stdlib-jdk8"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")


    implementation("commons-codec:commons-codec:1.14")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.apache.tuweni:tuweni-crypto:0.10.0")
    implementation("com.madgag.spongycastle:core:$spongyCastleVer")
    implementation("com.madgag.spongycastle:prov:$spongyCastleVer")
    implementation("com.madgag.spongycastle:bcpkix-jdk15on:$spongyCastleVer")
    implementation("com.madgag.spongycastle:bcpg-jdk15on:$spongyCastleVer")
    implementation("com.beust:klaxon:5.0.12")
    implementation("com.github.komputing:kbip44:0.1")
    implementation("com.github.walleth.kethereum:bip32:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39:$ketheriumVer")
    implementation("com.github.walleth.kethereum:bip39_wordlist_en:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_api:$ketheriumVer")
    implementation("com.github.walleth.kethereum:crypto_impl_bouncycastle:$ketheriumVer")
    implementation("com.github.walleth.kethereum:model:$ketheriumVer")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVer")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVer")

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
                name.set("libpylons")
                description.set("Library providing common functionality for interacting with the Pylons ecosystem")
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


