package com.crlsribeiro.reelcine.presentation.screens.groupdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.crlsribeiro.reelcine.R
import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.presentation.theme.Violet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    onNavigateBack: () -> Unit,
    onMovieClick: (Int) -> Unit = {},
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showCopied by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.group?.name ?: "Group", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.edit_profile_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Violet)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = onNavigateBack) { Text(stringResource(R.string.edit_profile_back), color = Violet) }
                    }
                }
            }
            uiState.group != null -> {
                val group = uiState.group!!
                LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding), contentPadding = PaddingValues(bottom = 80.dp)) {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(16.dp), color = Violet.copy(alpha = 0.2f), modifier = Modifier.size(64.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Group, contentDescription = null, tint = Violet, modifier = Modifier.size(32.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = group.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${group.memberCount} ${stringResource(R.string.group_members)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            if (group.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = group.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            if (group.inviteCode.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(shape = RoundedCornerShape(12.dp), color = Violet.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(text = stringResource(R.string.group_invite_code), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = group.inviteCode, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Violet, letterSpacing = 4.sp)
                                        }
                                        IconButton(onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("invite_code", group.inviteCode))
                                            showCopied = true
                                        }) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", tint = Violet)
                                        }
                                    }
                                }
                                if (showCopied) {
                                    LaunchedEffect(Unit) {
                                        kotlinx.coroutines.delay(2000)
                                        showCopied = false
                                    }
                                    Text(text = stringResource(R.string.group_code_copied), style = MaterialTheme.typography.bodySmall, color = Violet, modifier = Modifier.padding(top = 4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (!uiState.isAdmin) {
                                if (uiState.isMember) {
                                    OutlinedButton(onClick = { viewModel.leaveGroup() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                                        Text(stringResource(R.string.group_leave))
                                    }
                                } else {
                                    Button(onClick = { viewModel.joinGroup() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Color.White)) {
                                        Text(stringResource(R.string.group_join), fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Surface(shape = RoundedCornerShape(8.dp), color = Violet.copy(alpha = 0.15f), modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(R.string.group_admin), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = Violet, textAlign = TextAlign.Center)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = stringResource(R.string.group_recommendations), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uiState.recommendations.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = stringResource(R.string.group_no_recommendations), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = stringResource(R.string.group_no_recommendations_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    } else {
                        items(uiState.recommendations) { rec ->
                            RecommendationCard(recommendation = rec, onClick = { onMovieClick(rec.movieId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = recommendation.posterPath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(56.dp).height(84.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = recommendation.movieTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 2)
                if (recommendation.comment.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "\"${recommendation.comment}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "by ${recommendation.userName}", style = MaterialTheme.typography.labelSmall, color = Violet, fontSize = 11.sp)
            }
        }
    }
}
