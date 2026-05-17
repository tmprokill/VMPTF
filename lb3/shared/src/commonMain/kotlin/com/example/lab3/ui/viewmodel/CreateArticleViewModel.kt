package com.example.lab3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab3.data.AppDependencies
import com.example.lab3.data.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateArticleUiState(
    val title: String = "",
    val content: String = "",
    val excerpt: String = "",
    val selectedCategoryId: Int? = null,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CreateArticleViewModel : ViewModel() {
    private val _state = MutableStateFlow(CreateArticleUiState())
    val state: StateFlow<CreateArticleUiState> = _state

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            AppDependencies.apiService.getCategories()
                .onSuccess { cats ->
                    _state.value = _state.value.copy(categories = cats)
                }
        }
    }

    fun onTitleChange(v: String) { _state.value = _state.value.copy(title = v) }
    fun onContentChange(v: String) { _state.value = _state.value.copy(content = v) }
    fun onExcerptChange(v: String) { _state.value = _state.value.copy(excerpt = v) }
    fun onCategorySelected(id: Int?) { _state.value = _state.value.copy(selectedCategoryId = id) }

    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.title.isBlank()) {
            _state.value = s.copy(error = "Заголовок обов'язковий")
            return
        }
        if (s.content.isBlank()) {
            _state.value = s.copy(error = "Зміст обов'язковий")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            AppDependencies.apiService.createArticle(
                title = s.title.trim(),
                content = s.content.trim(),
                excerpt = s.excerpt.ifBlank { null },
                categoryId = s.selectedCategoryId
            ).onSuccess {
                _state.value = _state.value.copy(isLoading = false)
                onSuccess()
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Помилка створення статті"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
