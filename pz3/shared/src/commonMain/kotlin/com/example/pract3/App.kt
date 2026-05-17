package com.example.pract3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class Screen {
    MENU, GREETING, GUESS_NUMBER, RPS, ROMAN
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.MENU) }

        when (currentScreen) {
            Screen.MENU -> MainMenu(onNavigate = { currentScreen = it })
            Screen.GREETING -> GreetingScreen(onBack = { currentScreen = Screen.MENU })
            Screen.GUESS_NUMBER -> GuessNumberScreen(onBack = { currentScreen = Screen.MENU })
            Screen.RPS -> RpsScreen(onBack = { currentScreen = Screen.MENU })
            Screen.ROMAN -> RomanScreen(onBack = { currentScreen = Screen.MENU })
        }
    }
}

@Composable
fun MainMenu(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Практична робота 3", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        listOf(
            "1. Привітання" to Screen.GREETING,
            "2. Вгадай число" to Screen.GUESS_NUMBER,
            "3. Камінь-Ножиці-Папір" to Screen.RPS,
            "4. Римські цифри" to Screen.ROMAN
        ).forEach { (label, screen) ->
            Button(
                onClick = { onNavigate(screen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(label)
            }
        }
    }
}
