
plugins {
    kotlin("jvm") version("1.4.30")
    id("com.google.protobuf") version "0.8.15" apply false
    idea
    java
}


// todo: move to subprojects, but how?
ext["grpcVersion"] = "1.37.0"
ext["grpcKotlinVersion"] = "1.1.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["protobufVersion"] = "3.15.8"

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven(url = "https://jitpack.io")
    }
}
