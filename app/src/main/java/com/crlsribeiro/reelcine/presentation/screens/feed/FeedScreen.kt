package com.crlsribeiro.reelcine.presentation.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.presentation.theme.Gold
import com.crlsribeiro.reelcine.presentation.theme.SurfaceDark
import com.crlsribeiro.reelcine.presentation.theme.Violet
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FeedScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = viewModel.currentUserId

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Community Feed",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Discover what your friends are watching",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Violet)
            }
        } else if (uiState.recommendations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nenhuma recomendação ainda", style = MaterialTheme.typography.titleMedium)
                    Text("Seja o primeiro a recomendar um filme!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.recommendations) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        currentUserId = currentUserId,
                        onLikeClick = { viewModel.toggleLike(recommendation.id) },
                        onMovieClick = { onMovieClick(recommendation.movieId) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onMovieClick: () -> Unit
) {
    val isLiked = recommendation.likes.contains(currentUserId)
    val timeAgo = remember(recommendation.timestamp) {
        val diff = System.currentTimeMillis() - recommendation.timestamp
        when {
            diff < 3600000 -> "${diff / 60000}m atrás"
            diff < 86400000 -> "${diff / 3600000}h atrás"
            else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(recommendation.timestamp))
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Violet.copy(alpha = 0.3f), modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = recommendation.userName.firstOrNull()?.toString() ?: "?", fontWeight = FontWeight.Bold, color = Violet)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = recommendation.userName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = timeAgo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = recommendation.posterPath,
                    contentDescription = recommendation.movieTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(70.dp).height(100.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = recommendation.movieTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < recommendation.rating) Gold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (recommendation.comment.isNotBlank()) {
                        Text(text = "\"${recommendation.comment}\"", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Violet else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "${recommendation.likesCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun Modifier.background(color: androidx.compose.ui.graphics.Color) = this.then(
    Modifier.clip(RoundedCornerShape(8.dp))
)
