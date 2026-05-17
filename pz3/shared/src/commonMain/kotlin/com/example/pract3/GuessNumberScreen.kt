package com.example.pract3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun GuessNumberScreen(onBack: () -> Unit) {
    var secret by remember { mutableStateOf(Random.nextInt(1, 101)) }
    var input by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("Вгадайте число від 1 до 100") }
    var attempts by remember { mutableStateOf(0) }
    var won by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вгадай число", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Спроб: $attempts", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Text(hint, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        if (!won) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() } },
                label = { Text("Ваше число") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val guess = input.toIntOrNull()
                if (guess != null) {
                    attempts++
                    hint = when {
                        guess < secret -> "Більше! Спробуйте ще."
                        guess > secret -> "Менше! Спробуйте ще."
                        else -> {
                            won = true
                            "Вітаємо! Ви вгадали за $attempts спроб!"
                        }
                    }
                    input = ""
                }
            }) {
                Text("Вгадати")
            }
        } else {
            Button(onClick = {
                secret = Random.nextInt(1, 101)
                input = ""
                hint = "Вгадайте число від 1 до 100"
                attempts = 0
                won = false
            }) {
                Text("Нова гра")
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) {
            Text("Назад")
        }
    }
}
