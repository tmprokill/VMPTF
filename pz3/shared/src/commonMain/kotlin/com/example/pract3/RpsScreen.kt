package com.example.pract3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private val choices = listOf("Камінь", "Ножиці", "Папір")
private val choiceEmoji = listOf("✊", "✌️", "🖐️")

// wins[i] beats choices[wins[i]]
private fun beats(a: Int, b: Int) = (a == 0 && b == 1) || (a == 1 && b == 2) || (a == 2 && b == 0)

@Composable
fun RpsScreen(onBack: () -> Unit) {
    var playerChoice by remember { mutableStateOf(-1) }
    var computerChoice by remember { mutableStateOf(-1) }
    var result by remember { mutableStateOf("") }
    var wins by remember { mutableStateOf(0) }
    var losses by remember { mutableStateOf(0) }
    var draws by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Камінь-Ножиці-Папір", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Перемоги: $wins   Поразки: $losses   Нічиї: $draws",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))

        choices.indices.forEach { i ->
            Button(
                onClick = {
                    val comp = Random.nextInt(3)
                    playerChoice = i
                    computerChoice = comp
                    result = when {
                        i == comp -> { draws++; "Нічия!" }
                        beats(i, comp) -> { wins++; "Ви виграли!" }
                        else -> { losses++; "Комп'ютер виграв!" }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("${choiceEmoji[i]} ${choices[i]}")
            }
        }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Ви: ${choiceEmoji[playerChoice]} ${choices[playerChoice]}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Комп'ютер: ${choiceEmoji[computerChoice]} ${choices[computerChoice]}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                result,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = when (result) {
                    "Ви виграли!" -> MaterialTheme.colorScheme.primary
                    "Комп'ютер виграв!" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) {
            Text("Назад")
        }
    }
}
