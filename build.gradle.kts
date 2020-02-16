import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.github.ben-manes.versions") version Versions.Plugins.versions
}

dependencies {
    // KOTLIN
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    
    // KTOR
    fun ktor(suffix: String) = "io.ktor:ktor-$suffix:${Versions.ktor}"
    implementation(ktor("server-netty")) // or: "server-jetty", "server-tomcat"
    implementation(ktor("jackson")) // or: "serialization", "gson"
    implementation(ktor("auth"))
    implementation(ktor("html-builder"))
    
    // KTOR CLIENT
    implementation(ktor("client-core"))
    implementation(ktor("client-apache")) // or: "client-cio", "ktor-client-okhttp", ...
    implementation(ktor("client-jackson")) // or: "client-gson"
    
    // KODEIN
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:${Versions.kodein}")
    implementation("org.kodein.di:kodein-di-generic-jvm:${Versions.kodein}")
    
    // MISC
    implementation("io.github.microutils:kotlin-logging:${Versions.klogging}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")

    // TEST
    testImplementation(ktor("server-test-host")) {
        exclude(group = "junit", module = "junit") // use TestNG instead
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
    }
    testImplementation("org.testng:testng:${Versions.testng}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}")
    testImplementation("org.skyscreamer:jsonassert:${Versions.jsonAssert}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI")
        }
    }

    withType<Test> {
        useTestNG {}
    }

    withType<DependencyUpdatesTask> {
        val rejectPatterns = listOf("alpha", "beta", "eap", "rc").map { qualifier ->
            Regex("(?i).*[.-]$qualifier[.\\d-]*")
        }
        resolutionStrategy {
            componentSelection {
                all {
                    if (rejectPatterns.any { it.matches(candidate.version) }) {
                        reject("Release candidate")
                    }
                }
            }
        }
        checkForGradleUpdate = true
    }
}
