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
    id("com.github.johnrengelman.shadow") version Versions.Plugins.shadow
    application
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
    implementation(ktor("auth-jwt"))
    implementation(ktor("html-builder"))
    implementation(ktor("metrics"))
    implementation(ktor("metrics-micrometer"))
    implementation(ktor("freemarker"))
    implementation(ktor("mustache"))
    implementation(ktor("pebble"))
    implementation(ktor("thymeleaf"))
    implementation(ktor("velocity"))
    implementation(ktor("network"))

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

application {
    //    mainClassName = "ktorsamples._11_fatjarKt"
    mainClassName = "ktorsamples._51_auto_reloadKt"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI")
        }
    }

    withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to application.mainClassName
                )
            )
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
