package com.example.lab3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab3.data.Comment
import com.example.lab3.ui.viewmodel.ArticleDetailViewModel

@Composable
fun ArticleDetailScreen(
    articleId: Int,
    onBack: () -> Unit
) {
    val vm = remember { ArticleDetailViewModel(articleId) }
    val state by vm.state.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Видалити статтю") },
            text = { Text("Ви впевнені, що хочете видалити цю статтю?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        vm.deleteArticle(onBack)
                    }
                ) { Text("Видалити", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Скасувати") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Стаття") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< Назад") }
                },
                actions = {
                    if (vm.isCurrentUserAuthor()) {
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !state.isDeletingArticle
                        ) {
                            Text("Видалити", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoadingArticle) {
            Box(modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        state.error?.let { err ->
            Box(modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(err, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = vm::loadArticle) { Text("Повторити") }
                }
            }
            return@Scaffold
        }

        val article = state.article ?: return@Scaffold

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                article.category?.let { cat ->
                    Text(cat.name, style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                }
                Text(article.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Row {
                    Text("${article.author.username} · ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatDate(article.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(16.dp))
                Text(article.content, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("Коментарі (${state.comments.size})",
                    style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }

            if (state.isLoadingComments) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            } else {
                items(state.comments) { comment ->
                    CommentItem(comment)
                }
            }

            if (vm.isLoggedIn()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.commentText,
                        onValueChange = vm::onCommentTextChange,
                        label = { Text("Ваш коментар...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = vm::submitComment,
                        enabled = !state.isSubmittingComment && state.commentText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isSubmittingComment) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp)
                        } else {
                            Text("Додати коментар")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(comment.author.username, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(formatDate(comment.createdAt), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Text(comment.content, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatDate(dateStr: String): String = dateStr.take(10)
