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

application {
    mainClass.set("com.example.App") 
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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