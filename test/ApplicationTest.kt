package com.nooblabs

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {

    companion object {
        private val username = "u${LocalDateTime.now().nano}"
        private val email = "$username@mail.com"
        private val password = "pass1234"
    }

    @Test
    fun `Register User`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val response = client.post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "user" to mapOf(
                        "email" to email,
                        "password" to password,
                        "username" to username
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        response.bodyAsText().also { content ->
            assertNotNull(content)
            assertTrue(content.contains("email"))
            assertTrue(content.contains("username"))
            assertTrue(content.contains("bio"))
            assertTrue(content.contains("image"))
            assertTrue(content.contains("token"))
        }
    }


    @Test
    fun `Login User`() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "user" to mapOf(
                        "email" to email,
                        "password" to password
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        response.bodyAsText().also { content ->
            assertNotNull(content)
            assertTrue(content.contains("email"))
            assertTrue(content.contains("username"))
            assertTrue(content.contains("bio"))
            assertTrue(content.contains("image"))
            assertTrue(content.contains("token"))
        }
    }
}
