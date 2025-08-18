package com.example.choquality.proxy


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TotalFlowTest(
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
    data class TodoDto(val id: Int, val title: String, val content: String, val writer: String)

    data class TodoRes(
        val code: Int,
        val msg: String,
        val data: TodoDto,
        @com.fasterxml.jackson.annotation.JsonProperty("access_token")
        val accessToken: String?
    )

    @BeforeEach
    fun setUp() {
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Test
    fun `login test`() {
        client.get().uri("/init")
            .exchange()
            .expectStatus().isOk

        client.post().uri("/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg","name" to "1@gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk

        val login = client.post().uri("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(LoginRes::class.java)   // 타입 명시로 깔끔하게
            .returnResult()
            .responseBody!!
    }


    @Test
    fun `user test`() {
        client.get().uri("/init")
            .exchange()
            .expectStatus().isOk

        client.post().uri("/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg","name" to "1@gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk

        val login = client.post().uri("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(LoginRes::class.java)
            .returnResult()
            .responseBody!!

        val token = login.accessToken

        client.get().uri("/users/me")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)

        client.get().uri("/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data.id").isEqualTo(1)
            .jsonPath("$.data.email").isEqualTo("1@gg.gg")
            .jsonPath("$.data.name").isEqualTo("1@gg")


        client.put().uri("/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("name" to "변경이름"))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)

        client.get().uri("/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data.id").isEqualTo(1)
            .jsonPath("$.data.email").isEqualTo("1@gg.gg")
            .jsonPath("$.data.name").isEqualTo("변경이름")

        client.delete().uri("/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)

        client.get().uri("/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(7)
    }



    @Test
    fun `todo Test`() {
        client.get().uri("/init")
            .exchange()
            .expectStatus().isOk

        client.post().uri("/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg","name" to "1@gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk

        val login = client.post().uri("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("email" to "1@gg.gg", "password" to "1234"))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(LoginRes::class.java)
            .returnResult()
            .responseBody!!

        val token = login.accessToken

        client.post().uri("/todos")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("title" to "title","content" to "content"))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)

        client.get().uri("/todos")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data").isArray
            .jsonPath("$.data[0].id").isEqualTo(1)
            .jsonPath("$.data[0].title").isEqualTo("title")
            .jsonPath("$.data[0].content").isEqualTo("content")
            .jsonPath("$.data[0].writer").isEqualTo("1@gg")


        client.get().uri("/todos/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)

        val firstTodo =client.get().uri("/todos/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(TodoRes::class.java)
            .returnResult()
            .responseBody!!
        org.assertj.core.api.Assertions.assertThat(firstTodo.code).isEqualTo(0)
        org.assertj.core.api.Assertions.assertThat(firstTodo.data)
            .usingRecursiveComparison()
            .isEqualTo(TodoDto(id = 1, title = "title", content = "content", writer = "1@gg"))

        client.get().uri("/todos/2")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").value<Int> { actual ->
                org.assertj.core.api.Assertions.assertThat(actual).isNotEqualTo(0)
            }

        val res = client.put().uri("/todos/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("title" to "1title1","content" to "1content1"))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(TodoRes::class.java)
            .returnResult()
            .responseBody!!
        org.assertj.core.api.Assertions.assertThat(res.code).isEqualTo(0)
        org.assertj.core.api.Assertions.assertThat(res.data)
            .usingRecursiveComparison()
            .isEqualTo(TodoDto(id = 1, title = "1title1", content = "1content1", writer = "1@gg"))

        client.get().uri("/todos/search?title=1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data").isArray
            .jsonPath("$.data[0].id").isEqualTo(1)
            .jsonPath("$.data[0].title").isEqualTo("1title1")
            .jsonPath("$.data[0].content").isEqualTo("1content1")
            .jsonPath("$.data[0].writer").isEqualTo("1@gg")

        client.get().uri("/todos/search?title=2")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)
            .jsonPath("$.data").isEmpty

        client.delete().uri("/todos/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo(0)

        client.get().uri("/todos/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").value<Int> { actual ->
                org.assertj.core.api.Assertions.assertThat(actual).isNotEqualTo(0)
            }

    }
}