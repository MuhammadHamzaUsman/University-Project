plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics"
    )
}

sourceSets {
    main {
        resources {
            srcDirs("app/src/main/resources", "app/assets")
        }
    }
}

application {
    mainClass.set("com.example.MainMenu") 
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    sourceSets["main"].java {
        srcDir("app/levelsOrgin")
    }

    sourceSets["main"].java {
        srcDir("app/levels")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.jmonkeyengine.org/releases")
}

val jmeVer = "3.8.1-stable"

dependencies {

    implementation("org.jmonkeyengine:jme3-core:$jmeVer")
    implementation("org.jmonkeyengine:jme3-desktop:$jmeVer")
    implementation("org.jmonkeyengine:jme3-lwjgl3:$jmeVer")
    implementation("org.jmonkeyengine:jme3-plugins:$jmeVer")

    implementation("org.fxmisc.richtext:richtextfx:0.11.7")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    modularity.inferModulePath.set(false)
}

tasks.withType<JavaExec> {
    modularity.inferModulePath.set(false)
    jvmArgs = listOf(
        "--add-modules=javafx.controls,javafx.fxml,javafx.graphics"
    )
}