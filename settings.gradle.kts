/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/5.2.1/userguide/multi_project_builds.html
 */

rootProject.name = "pylons_wallet"

//include(":txutil", ":walletcore", ":devwallet", ":httpipc", ":devdevwallet", ":libkylons")
//protobuf gRPC support addition: Tierre
include(":protos", ":proto-stub", ":proto-stub-lite", ":txutil", ":walletcore", ":devwallet", ":httpipc", ":devdevwallet", ":libkylons")


pluginManagement {
    repositories {
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
}