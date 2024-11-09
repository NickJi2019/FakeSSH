import org.gradle.kotlin.dsl.test
import org.gradle.kotlin.dsl.testImplementation

plugins {
    kotlin("jvm") version "2.0.21" // Kotlin version to use
}

group = "com.woznes" // A company name, for example, `org.jetbrains`
version = "1.0-SNAPSHOT" // Version to assign to the built artifact

repositories { // Sources of dependencies. See 1️⃣
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://packages.jetbrains.team/maven/p/amper/amper")
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
}

dependencies { // All the libraries you want to use. See 3️⃣
    // Copy dependencies' names after you find them in a repository
    testImplementation(kotlin("test")) // The Kotlin test library
    implementation("org.apache.sshd:sshd-core:+") //
    implementation("org.apache.commons:commons-csv:+") //
}

tasks.test { // See 4️⃣
    useJUnitPlatform() // JUnitPlatform for tests. See 5️⃣
}