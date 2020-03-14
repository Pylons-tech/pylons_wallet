import kotlin.collections.*

plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.17.2"
    application
}

group = "com.pylons"
version = "0.1a"
val embeddedJRE = true

dependencies {
    implementation("no.tornado", "tornadofx", "1.7.17")
    testCompile("junit", "junit", "4.12")
    implementation(kotlin("stdlib-jdk8"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        sourceCompatibility = "11"
    }
}

javafx {
    modules("javafx.base", "javafx.controls", "javafx.graphics", "javafx.fxml")
    configuration = "implementation"
}

jlink {
    addExtraDependencies("javafx")
    launcher {
        name = "Pylons DevWallet"
    }

    jpackage {
        jpackageHome = "C:\\Program Files\\Java\\jdk-14"
        skipInstaller = true
        imageName = "Pylons DevWallet"
    }
}

application {
    applicationName = "Pylons DevWallet"
    mainClassName = "com.pylons.devwallet.DevWalletApp"
}

dependencies {
    implementation(project(":walletcore"))
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("com.google.protobuf:protobuf-java:3.11.4")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Class-Path"] = configurations.compile.get().joinToString { "$name " }
        attributes["Main-Class"] = application.mainClassName
    }

     for (it in configurations.runtimeClasspath.get().files) {
         var f = it
         if (f.isDirectory) f = zipTree(f).singleFile
         exclude("META-INF/MANIFEST.MF")
         exclude("META-INF/*.SF")
         exclude("META-INF/*.DSA")
         exclude("META-INF/*.RSA")
     }
}

val libsDir = property("libsDir") as File

task<Copy>("copyDependencies") {
    println(libsDir.absolutePath)
    destinationDir = libsDir
    from(configurations.runtime)
}

task<Exec>("javapackager") {
    dependsOn("assemble", "copyDependencies")
    var nativeType : String? = null
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        nativeType = "msi"
    }
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
        nativeType = "dmg"
    }
    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
        nativeType = "rpm"
    }
    val dep = mutableListOf<String>()
    for (file in configurations.runtime.get().all) {
        dep.add("-srcfiles")
        dep.add(file.name)
    }
    val paramEmbeddedJRE =
            if (embeddedJRE) listOf("")
            else listOf("-Bruntime=")
    workingDir = project.projectDir
    commandLine = listOf(
            "jpackager",
            "create-image",
            "-i", buildDir.path + "/libs",
            "-j", jar.archiveFileName.get(),
            "-c", application.mainClassName,
            "-n", application.applicationName,
            "-o", "${buildDir}/distribution",
            "--singleton"
    )
    println(commandLine)
}