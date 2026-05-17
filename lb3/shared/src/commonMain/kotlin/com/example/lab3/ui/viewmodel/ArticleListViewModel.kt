package com.example.lab3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab3.data.AppDependencies
import com.example.lab3.data.Article
import com.example.lab3.data.Category
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ArticleListUiState(
    val articles: List<Article> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val search: String = "",
    val selectedCategoryId: Int? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1
)

class ArticleListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ArticleListUiState())
    val state: StateFlow<ArticleListUiState> = _state

    private var searchJob: Job? = null

    init {
        loadCategories()
        loadArticles()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            AppDependencies.apiService.getCategories()
                .onSuccess { cats ->
                    _state.value = _state.value.copy(categories = cats)
                }
        }
    }

    fun loadArticles(page: Int = 1) {
        val s = _state.value
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null, currentPage = page)
            AppDependencies.apiService.getArticles(
                search = s.search.ifBlank { null },
                categoryId = s.selectedCategoryId,
                page = page
            ).onSuccess { response ->
                _state.value = _state.value.copy(
                    articles = response.articles,
                    totalPages = response.totalPages,
                    isLoading = false
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Помилка завантаження"
                )
            }
        }
    }

    fun onSearchChange(query: String) {
        _state.value = _state.value.copy(search = query, currentPage = 1)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            loadArticles(1)
        }
    }

    fun onCategorySelected(categoryId: Int?) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId, currentPage = 1)
        loadArticles(1)
    }

    fun onPageChange(page: Int) {
        loadArticles(page)
    }

    fun refresh() {
        loadArticles(_state.value.currentPage)
    }
}
