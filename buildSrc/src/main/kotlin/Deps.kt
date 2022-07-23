const val kotlinVersion = "1.7.10"
const val ktorVersion = "2.0.3"
const val logbackVersion = "1.2.3"
const val h2Version = "2.1.214"
const val exposedVersion = "0.38.2"
const val hikariVersion = "5.0.1"
const val koinVersion = "3.2.0"

object Deps {
    const val ktorServerCore = "io.ktor:ktor-server-core:$ktorVersion"
    const val ktorServerNetty = "io.ktor:ktor-server-netty:$ktorVersion"
    const val ktorAuth = "io.ktor:ktor-server-auth:$ktorVersion"
    const val ktorAuthJwt = "io.ktor:ktor-server-auth-jwt:$ktorVersion"
    const val ktorContentNegotiation = "io.ktor:ktor-server-content-negotiation:$ktorVersion"
    const val ktorJackson = "io.ktor:ktor-serialization-jackson:$ktorVersion"
    const val ktorDefaultHeaders = "io.ktor:ktor-server-default-headers:$ktorVersion"
    const val ktorCors = "io.ktor:ktor-server-cors:$ktorVersion"
    const val ktorCallLogging = "io.ktor:ktor-server-call-logging:$ktorVersion"
    const val ktorStatusPages = "io.ktor:ktor-server-status-pages:$ktorVersion"
    const val ktorTests = "io.ktor:ktor-server-test-host:$ktorVersion"
    const val ktorClientContentNegotiation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"

    const val logback = "ch.qos.logback:logback-classic:$logbackVersion"

    const val h2Database = "com.h2database:h2:$h2Version"
    const val exposedCore = "org.jetbrains.exposed:exposed-core:$exposedVersion"
    const val exposedDao = "org.jetbrains.exposed:exposed-dao:$exposedVersion"
    const val exposedJdbc = "org.jetbrains.exposed:exposed-jdbc:$exposedVersion"
    const val exposedJavaTime = "org.jetbrains.exposed:exposed-java-time:$exposedVersion"
    const val hikari = "com.zaxxer:HikariCP:$hikariVersion"

    const val koin = "io.insert-koin:koin-ktor:$koinVersion"

    const val kotlinTests = "org.jetbrains.kotlin:kotlin-test:$kotlinVersion"
}