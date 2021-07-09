group = "tech.pylons"
version = "0.1.0-SNAPSHOT.0"

plugins {
    kotlin("jvm") version("1.4.30")
    id("com.google.protobuf") version "0.8.15" apply false
    idea
    java
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set("S8SKCvQx") // defaults to project.properties["myNexusUsername"]
            password.set("IpV199ViKHp2RBmT32dMkMKqplXKC9vnsDnW/6HEIb0Q") // defaults to project.properties["myNexusPassword"]
        }
    }
}


// todo: move to subprojects, but how?
ext["grpcVersion"] = "1.37.0"
ext["grpcKotlinVersion"] = "1.1.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["protobufVersion"] = "3.15.8"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        google()
        maven(url = "https://jitpack.io")
    }
}
