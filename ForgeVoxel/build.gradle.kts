plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

nexusPublishing {
    repositories {
        sonatype()
    }
}
