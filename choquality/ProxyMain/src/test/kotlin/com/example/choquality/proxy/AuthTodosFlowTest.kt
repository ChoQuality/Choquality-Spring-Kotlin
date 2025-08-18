package com.example.choquality.proxy


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthTodosFlowTest(
    @LocalServerPort private val port: Int
) {

    private lateinit var client: WebTestClient

    data class LoginRes(
        val code: Int,
        val msg: String,
        val data: Any?,
        @com.fasterxml.jackson.annotation.JsonProperty("access_token")
        val accessToken: String
    )

    @BeforeEach
    fun setUp() {
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Test
    fun `login then call todos with Bearer token`() {
        // 1) 로그인 → access_token 추출
        val login = client.post().uri("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(LoginRes::class.java)   // 타입 명시로 깔끔하게
            .returnResult()
            .responseBody!!

        val token = login.accessToken

        // 2) Bearer 토큰으로 /todos 호출
        // 컨트롤러가 @GetMapping("/todos")라면 GET 사용, POST면 .post()로 변경
        client.get().uri("/todos")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data").isArray
    }
}