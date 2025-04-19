//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

//tasks.withType<KotlinCompile>() {
//    kotlinOptions {
//        jvmTarget = "21"
//    }
//}

group = "realworld"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

//kotlin.sourceSets["main"].kotlin.srcDirs("src")
//kotlin.sourceSets["test"].kotlin.srcDirs("test")
//
//sourceSets["main"].resources.srcDirs("resources")
//sourceSets["test"].resources.srcDirs("testresources")

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.auth)
    implementation(libs.ktor.auth.jwt)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.jackson)
    implementation(libs.ktor.default.headers)
    implementation(libs.ktor.cors)
    implementation(libs.ktor.call.logging)
    implementation(libs.ktor.status.pages)
    implementation(libs.ktor.client.content.negotiation)

    // Logging
    implementation(libs.logback)

    // Database (Exposed & H2)
    implementation(libs.h2.database)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.hikari)

    // Dependency Injection
    implementation(libs.koin.ktor)

    // Testing dependencies
    testImplementation(libs.kotlin.tests)
    testImplementation(libs.ktor.tests) // Ktor test dependency
}
