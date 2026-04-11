package com.carlosribeiro.reelcine.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.presentation.theme.Gold
import com.carlosribeiro.reelcine.presentation.theme.SurfaceDark
import com.carlosribeiro.reelcine.presentation.theme.Violet

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Buscar", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.onQueryChange(it) },
            placeholder = { Text("Buscar filmes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Violet) },
            trailingIcon = {
                if (uiState.query.isNotBlank()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Violet,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Violet)
                }
            }
            uiState.isEmpty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😕", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nenhum filme encontrado", style = MaterialTheme.typography.titleMedium)
                        Text("Tente buscar outro título", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            uiState.movies.isNotEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.movies, key = { it.id }) { movie ->
                        SearchMovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎬", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Busque seu filme favorito", style = MaterialTheme.typography.titleMedium)
                        Text("Digite pelo menos 2 caracteres", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchMovieCard(movie: Movie, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageRequest = remember(movie.posterPath) {
        ImageRequest.Builder(context).data(movie.posterPath).crossfade(false)
            .memoryCacheKey(movie.posterPath).diskCacheKey(movie.posterPath).size(200, 300).build()
    }
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.67f)
            .clip(RoundedCornerShape(10.dp)).clickable(onClick = onClick)
    ) {
        AsyncImage(model = imageRequest, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(SurfaceDark))
        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(4.dp)
                .background(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "★", color = Gold, fontSize = 9.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = String.format("%.1f", movie.voteAverage), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
