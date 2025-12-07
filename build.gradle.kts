plugins {
    kotlin("jvm") version "1.9.21"
}

group = "com.sergbloim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
    testImplementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(15)
}