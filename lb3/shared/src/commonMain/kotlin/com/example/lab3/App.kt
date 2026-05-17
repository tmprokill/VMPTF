package com.example.lab3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.lab3.data.AppDependencies
import com.example.lab3.ui.screens.ArticleDetailScreen
import com.example.lab3.ui.screens.ArticleListScreen
import com.example.lab3.ui.screens.CreateArticleScreen
import com.example.lab3.ui.screens.LoginScreen
import com.example.lab3.ui.screens.RegisterScreen

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object ArticleList : Screen()
    data class ArticleDetail(val id: Int) : Screen()
    object CreateArticle : Screen()
}

@Composable
fun App() {
    val initialScreen: Screen = if (AppDependencies.tokenStorage.getToken() != null) {
        Screen.ArticleList
    } else {
        Screen.Login
    }

    var currentScreen by remember { mutableStateOf<Screen>(initialScreen) }

    MaterialTheme {
        when (val screen = currentScreen) {
            is Screen.Login -> LoginScreen(
                onLoginSuccess = { currentScreen = Screen.ArticleList },
                onNavigateToRegister = { currentScreen = Screen.Register }
            )
            is Screen.Register -> RegisterScreen(
                onRegisterSuccess = { currentScreen = Screen.ArticleList },
                onNavigateToLogin = { currentScreen = Screen.Login }
            )
            is Screen.ArticleList -> ArticleListScreen(
                onArticleClick = { id -> currentScreen = Screen.ArticleDetail(id) },
                onCreateArticle = { currentScreen = Screen.CreateArticle },
                onLogout = {
                    AppDependencies.tokenStorage.clearToken()
                    currentScreen = Screen.Login
                }
            )
            is Screen.ArticleDetail -> ArticleDetailScreen(
                articleId = screen.id,
                onBack = { currentScreen = Screen.ArticleList }
            )
            is Screen.CreateArticle -> CreateArticleScreen(
                onSuccess = { currentScreen = Screen.ArticleList },
                onBack = { currentScreen = Screen.ArticleList }
            )
        }
    }
}
