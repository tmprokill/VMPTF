package com.example.lab3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab3.ui.viewmodel.CreateArticleViewModel

@Composable
fun CreateArticleScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val vm = remember { CreateArticleViewModel() }
    val state by vm.state.collectAsState()

    var showCategoryMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Нова стаття") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< Назад") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = vm::onTitleChange,
                label = { Text("Заголовок *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.excerpt,
                onValueChange = vm::onExcerptChange,
                label = { Text("Короткий опис (необов'язково)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = state.content,
                onValueChange = vm::onContentChange,
                label = { Text("Зміст *") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                minLines = 5
            )

            Box {
                OutlinedButton(
                    onClick = { showCategoryMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        state.categories.find { it.id == state.selectedCategoryId }?.name
                            ?: "Оберіть категорію (необов'язково)"
                    )
                }
                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Без категорії") },
                        onClick = {
                            vm.onCategorySelected(null)
                            showCategoryMenu = false
                        }
                    )
                    state.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                vm.onCategorySelected(cat.id)
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            state.error?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = { vm.submit(onSuccess) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Опублікувати статтю")
                }
            }
        }
    }
}
