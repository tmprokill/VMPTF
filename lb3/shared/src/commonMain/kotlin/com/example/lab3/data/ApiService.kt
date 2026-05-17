package com.example.lab3.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ApiService(
    private val tokenStorage: TokenStorage,
    private val baseUrl: String = "http://10.0.2.2:5000"
) {
    private val client: HttpClient = createHttpClient()

    private fun bearerToken(): String? = tokenStorage.getToken()?.let { "Bearer $it" }

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        val response: HttpResponse = client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        if (response.status.value !in 200..299) {
            error(response.body<ErrorResponse>().error)
        }
        response.body()
    }

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> = runCatching {
        val response: HttpResponse = client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, email, password))
        }
        if (response.status.value !in 200..299) {
            error(response.body<ErrorResponse>().error)
        }
        response.body()
    }

    suspend fun getMe(): Result<User> = runCatching {
        val token = bearerToken() ?: error("No token")
        client.get("$baseUrl/api/auth/me") {
            headers { append(HttpHeaders.Authorization, token) }
        }.body()
    }

    suspend fun getArticles(
        search: String? = null,
        categoryId: Int? = null,
        page: Int = 1
    ): Result<ArticlesResponse> = runCatching {
        val params = buildList {
            if (!search.isNullOrBlank()) add("search=${search.trim()}")
            if (categoryId != null) add("category_id=$categoryId")
            add("page=$page")
            add("limit=9")
        }.joinToString("&")
        client.get("$baseUrl/api/articles?$params").body()
    }

    suspend fun getArticle(id: Int): Result<Article> = runCatching {
        client.get("$baseUrl/api/articles/$id").body()
    }

    suspend fun createArticle(
        title: String,
        content: String,
        excerpt: String?,
        categoryId: Int?
    ): Result<Unit> = runCatching {
        val token = bearerToken() ?: error("No token")
        val response: HttpResponse = client.post("$baseUrl/api/articles") {
            contentType(ContentType.Application.Json)
            headers { append(HttpHeaders.Authorization, token) }
            setBody(CreateArticleRequest(title, content, excerpt?.ifBlank { null }, categoryId))
        }
        if (response.status.value !in 200..299) {
            val err = response.body<ErrorResponse>()
            error(err.error)
        }
    }

    suspend fun deleteArticle(id: Int): Result<Unit> = runCatching {
        val token = bearerToken() ?: error("No token")
        val response: HttpResponse = client.delete("$baseUrl/api/articles/$id") {
            headers { append(HttpHeaders.Authorization, token) }
        }
        if (response.status.value !in 200..299) {
            val err = response.body<ErrorResponse>()
            error(err.error)
        }
    }

    suspend fun getComments(articleId: Int): Result<List<Comment>> = runCatching {
        client.get("$baseUrl/api/comments/$articleId").body()
    }

    suspend fun addComment(articleId: Int, content: String): Result<Comment> = runCatching {
        val token = bearerToken() ?: error("No token")
        val response: HttpResponse = client.post("$baseUrl/api/comments/$articleId") {
            contentType(ContentType.Application.Json)
            headers { append(HttpHeaders.Authorization, token) }
            setBody(CreateCommentRequest(content))
        }
        if (response.status.value !in 200..299) {
            val err = response.body<ErrorResponse>()
            error(err.error)
        }
        response.body()
    }

    suspend fun getCategories(): Result<List<Category>> = runCatching {
        client.get("$baseUrl/api/categories").body()
    }
}
