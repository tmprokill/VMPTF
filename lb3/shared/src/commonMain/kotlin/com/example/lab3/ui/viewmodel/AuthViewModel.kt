package com.example.lab3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab3.data.AppDependencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Введіть email та пароль")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            AppDependencies.apiService.login(email, password)
                .onSuccess { response ->
                    AppDependencies.tokenStorage.saveToken(response.token)
                    _state.value = _state.value.copy(isLoading = false, success = true)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Помилка входу"
                    )
                }
        }
    }

    fun register(username: String, email: String, password: String, onSuccess: () -> Unit) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Всі поля обов'язкові")
            return
        }
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "Пароль мінімум 6 символів")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            AppDependencies.apiService.register(username, email, password)
                .onSuccess { response ->
                    AppDependencies.tokenStorage.saveToken(response.token)
                    _state.value = _state.value.copy(isLoading = false, success = true)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Помилка реєстрації"
                    )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
