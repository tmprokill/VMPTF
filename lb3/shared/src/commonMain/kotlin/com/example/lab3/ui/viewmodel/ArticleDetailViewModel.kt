package com.example.lab3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab3.data.AppDependencies
import com.example.lab3.data.Article
import com.example.lab3.data.Comment
import com.example.lab3.decodeBase64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val article: Article? = null,
    val comments: List<Comment> = emptyList(),
    val isLoadingArticle: Boolean = false,
    val isLoadingComments: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val isDeletingArticle: Boolean = false,
    val error: String? = null,
    val commentText: String = "",
    val isDeleted: Boolean = false
)

class ArticleDetailViewModel(private val articleId: Int) : ViewModel() {
    private val _state = MutableStateFlow(ArticleDetailUiState())
    val state: StateFlow<ArticleDetailUiState> = _state

    private val currentUserId: Int?
        get() = AppDependencies.tokenStorage.getToken()?.let { parseUserIdFromToken(it) }

    init {
        loadArticle()
        loadComments()
    }

    fun loadArticle() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingArticle = true, error = null)
            AppDependencies.apiService.getArticle(articleId)
                .onSuccess { article ->
                    _state.value = _state.value.copy(article = article, isLoadingArticle = false)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoadingArticle = false,
                        error = e.message ?: "Помилка завантаження"
                    )
                }
        }
    }

    fun loadComments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingComments = true)
            AppDependencies.apiService.getComments(articleId)
                .onSuccess { comments ->
                    _state.value = _state.value.copy(comments = comments, isLoadingComments = false)
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoadingComments = false)
                }
        }
    }

    fun onCommentTextChange(text: String) {
        _state.value = _state.value.copy(commentText = text)
    }

    fun submitComment() {
        val text = _state.value.commentText.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmittingComment = true, error = null)
            AppDependencies.apiService.addComment(articleId, text)
                .onSuccess { comment ->
                    _state.value = _state.value.copy(
                        comments = _state.value.comments + comment,
                        commentText = "",
                        isSubmittingComment = false
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isSubmittingComment = false,
                        error = e.message ?: "Помилка додавання коментаря"
                    )
                }
        }
    }

    fun deleteArticle(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDeletingArticle = true, error = null)
            AppDependencies.apiService.deleteArticle(articleId)
                .onSuccess {
                    _state.value = _state.value.copy(isDeletingArticle = false, isDeleted = true)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isDeletingArticle = false,
                        error = e.message ?: "Помилка видалення"
                    )
                }
        }
    }

    fun isCurrentUserAuthor(): Boolean {
        val article = _state.value.article ?: return false
        val userId = currentUserId ?: return false
        return article.author.id == userId
    }

    fun isLoggedIn(): Boolean = AppDependencies.tokenStorage.getToken() != null

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

private fun parseUserIdFromToken(token: String): Int? {
    return try {
        val payload = token.split(".").getOrNull(1) ?: return null
        val padded = payload + "=".repeat((4 - payload.length % 4) % 4)
        val decoded = decodeBase64(padded)
        val idMatch = Regex("\"id\":(\\d+)").find(decoded)
        idMatch?.groupValues?.get(1)?.toIntOrNull()
    } catch (e: Exception) {
        null
    }
}
