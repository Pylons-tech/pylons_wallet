import kotlin.collections.*
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    java
    kotlin("jvm")
    id("com.google.protobuf")
}

group = "com.pylons.lib"
version = "0.1"
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
    //implementation(project(":proto-stub"))
    protobuf(project(":protos"))

    implementation(kotlin("stdlib-jdk8"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")


    implementation("com.google.guava:guava:28.2-jre")
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

    //implementation("com.google.protobuf:protobuf-java:${rootProject.ext["protobufVersion"]}")
    //implementation("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")

    api("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    api("com.google.protobuf:protobuf-java:${rootProject.ext["protobufVersion"]}")
    api("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    api("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
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

sourceSets["main"].java.srcDir("build/generated/source/proto/main/java")

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.ext["protobufVersion"]}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.ext["grpcVersion"]}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${rootProject.ext["grpcKotlinVersion"]}:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}