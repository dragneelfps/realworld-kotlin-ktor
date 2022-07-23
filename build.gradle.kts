import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version kotlinVersion
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "18"
    }
}

group = "realworld"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

repositories {
    mavenCentral()
}

dependencies {
    implementation(Deps.ktorServerCore)
    implementation(Deps.ktorServerNetty)
    implementation(Deps.ktorAuth)
    implementation(Deps.ktorAuthJwt)
    implementation(Deps.ktorContentNegotiation)
    implementation(Deps.ktorJackson)
    implementation(Deps.ktorDefaultHeaders)
    implementation(Deps.ktorCors)
    implementation(Deps.ktorCallLogging)
    implementation(Deps.ktorStatusPages)

    implementation(Deps.logback)
    implementation(Deps.h2Database)
    implementation(Deps.exposedCore)
    implementation(Deps.exposedDao)
    implementation(Deps.exposedJdbc)
    implementation(Deps.exposedJavaTime)
    implementation(Deps.hikari)
    implementation(Deps.koin)

    testImplementation(Deps.kotlinTests)
    testImplementation(Deps.ktorTests)
    testImplementation(Deps.ktorClientContentNegotiation)
}
