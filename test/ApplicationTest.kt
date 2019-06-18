package com.nooblabs

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/api").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Welcome to Realworld", response.content)
            }
        }
    }
}
