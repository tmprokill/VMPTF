package com.example.lab3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lab3.data.Article
import com.example.lab3.ui.viewmodel.ArticleListViewModel

@Composable
fun ArticleListScreen(
    onArticleClick: (Int) -> Unit,
    onCreateArticle: () -> Unit,
    onLogout: () -> Unit
) {
    val vm = remember { ArticleListViewModel() }
    val state by vm.state.collectAsState()

    var showCategoryMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Блог") },
                actions = {
                    TextButton(onClick = onLogout) { Text("Вийти") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onCreateArticle) {
                Text("+ Стаття")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = state.search,
                onValueChange = vm::onSearchChange,
                label = { Text("Пошук статей...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    OutlinedButton(onClick = { showCategoryMenu = true }) {
                        Text(
                            state.categories.find { it.id == state.selectedCategoryId }?.name
                                ?: "Всі категорії"
                        )
                    }
                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Всі категорії") },
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
            }

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = vm::refresh) { Text("Повторити") }
                    }
                }
            } else if (state.articles.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Статей не знайдено", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.articles) { article ->
                        ArticleListItem(article = article, onClick = { onArticleClick(article.id) })
                    }
                }

                if (state.totalPages > 1) {
                    PaginationRow(
                        currentPage = state.currentPage,
                        totalPages = state.totalPages,
                        onPageChange = vm::onPageChange
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleListItem(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            article.category?.let { cat ->
                Text(
                    cat.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(article.title, style = MaterialTheme.typography.titleMedium, maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            article.excerpt?.let { excerpt ->
                Text(excerpt, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(article.author.username, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${article.commentsCount} коментарів",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PaginationRow(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) { Text("< Попередня") }

        Text(
            "$currentPage / $totalPages",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        TextButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) { Text("Наступна >") }
    }
}
