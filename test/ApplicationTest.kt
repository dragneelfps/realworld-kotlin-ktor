package com.nooblabs

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
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
    fun `Register User`() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/api/users") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    "{\n  \"user\": {\n    \"email\": \"$email\",\n    \"password\": \"$password\",\n    \"username\": \"$username\"\n  }\n}"
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)
                assertTrue(content.contains("email"))
                assertTrue(content.contains("username"))
                assertTrue(content.contains("bio"))
                assertTrue(content.contains("image"))
                assertTrue(content.contains("token"))
            }
        }
    }

    @Test
    fun `Login User`() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/api/users/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    "{\n  \"user\": {\n    \"email\": \"$email\",\n    \"password\": \"$password\"\n  }\n}"
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)
                assertTrue(content.contains("email"))
                assertTrue(content.contains("username"))
                assertTrue(content.contains("bio"))
                assertTrue(content.contains("image"))
                assertTrue(content.contains("token"))
            }
        }
    }
}
