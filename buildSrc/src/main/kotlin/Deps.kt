const val kotlinVersion = "1.5.10"
const val ktorVersion = "1.6.0"
const val logbackVersion = "1.2.3"
const val h2Version = "1.4.200"
const val exposedVersion = "0.32.1"
const val hikariVersion = "4.0.3"
const val koinVersion = "3.1.0"

object Deps {
    const val kotlinLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    const val ktorServerNetty = "io.ktor:ktor-server-netty:$ktorVersion"
    const val ktorAuthJwt = "io.ktor:ktor-auth-jwt:$ktorVersion"
    const val ktorJackson = "io.ktor:ktor-jackson:$ktorVersion"
    const val ktorTests = "io.ktor:ktor-server-tests:$ktorVersion"

    const val logback = "ch.qos.logback:logback-classic:$logbackVersion"

    const val h2Database = "com.h2database:h2:$h2Version"
    const val exposedCore = "org.jetbrains.exposed:exposed-core:$exposedVersion"
    const val exposedDao = "org.jetbrains.exposed:exposed-dao:$exposedVersion"
    const val exposedJdbc = "org.jetbrains.exposed:exposed-jdbc:$exposedVersion"
    const val exposedJavaTime = "org.jetbrains.exposed:exposed-java-time:$exposedVersion"
    const val hikari = "com.zaxxer:HikariCP:$hikariVersion"
    const val koin = "io.insert-koin:koin-ktor:$koinVersion"
}