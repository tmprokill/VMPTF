package com.example.lab3.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRef(
    val id: Int,
    val username: String
)

@Serializable
data class CategoryRef(
    val id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class Article(
    val id: Int,
    val title: String,
    val content: String = "",
    val excerpt: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String = "",
    val author: UserRef,
    val category: CategoryRef? = null,
    @SerialName("comments_count") val commentsCount: Int = 0
)

@Serializable
data class Comment(
    val id: Int,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    val author: UserRef
)

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class ArticlesResponse(
    val articles: List<Article>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class CreateArticleRequest(
    val title: String,
    val content: String,
    val excerpt: String? = null,
    @SerialName("category_id") val categoryId: Int? = null
)

@Serializable
data class CreateCommentRequest(
    val content: String
)

@Serializable
data class ErrorResponse(
    val error: String
)
