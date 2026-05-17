package com.example.pract3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private fun romanToArabic(roman: String): Int? {
    if (roman.isBlank()) return null
    val values = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
    val upper = roman.uppercase().trim()
    if (upper.any { it !in values }) return null
    var result = 0
    var prev = 0
    for (ch in upper.reversed()) {
        val curr = values[ch]!!
        result += if (curr < prev) -curr else curr
        prev = curr
    }
    return if (result > 0) result else null
}

@Composable
fun RomanScreen(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Римські → Арабські", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Введіть римське число (наприклад: XIV, MCMXCIX)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it.uppercase()
                output = ""
                isError = false
            },
            label = { Text("Римське число") },
            singleLine = true,
            isError = isError
        )
        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val result = romanToArabic(input)
            if (result != null) {
                output = "$input = $result"
                isError = false
            } else {
                output = "Невірне введення"
                isError = true
            }
        }) {
            Text("Конвертувати")
        }

        if (output.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                output,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) {
            Text("Назад")
        }
    }
}
