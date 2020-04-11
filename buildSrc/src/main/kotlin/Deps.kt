const val kotlinVersion = "1.3.70"
const val ktorVersion = "1.3.2"
const val logbackVersion = "1.2.1"
const val h2Version = "1.4.200"
const val exposedVersion = "0.17.7"
const val hikariVersion = "3.4.2"

object Deps {
    const val kotlinLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    const val ktorServerNetty = "io.ktor:ktor-server-netty:$ktorVersion"
    const val ktorAuthJwt = "io.ktor:ktor-auth-jwt:$ktorVersion"
    const val ktorJackson = "io.ktor:ktor-jackson:$ktorVersion"
    const val ktorTests = "io.ktor:ktor-server-tests:$ktorVersion"

    const val logback = "ch.qos.logback:logback-classic:$logbackVersion"

    const val h2Database = "com.h2database:h2:$h2Version"
    const val exposed = "org.jetbrains.exposed:exposed:$exposedVersion"
    const val hikari = "com.zaxxer:HikariCP:$hikariVersion"
}