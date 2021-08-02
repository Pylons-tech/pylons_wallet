group = "tech.pylons"
version = "0.1.0"

// todo: maybe use variants / configurations to do both stub & stub-lite here

// Note: We use the java-library plugin to get the protos into the artifact for this subproject
// because there doesn't seem to be an better way.
plugins {
    `java-library`
    `maven-publish`
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("Pylons protos")
                description.set("Protos for use by Pylons ecosystem software")
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