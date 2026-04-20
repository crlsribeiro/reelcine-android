package com.crlsribeiro.reelcine.presentation.screens.movielist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.presentation.theme.Gold
import com.crlsribeiro.reelcine.presentation.theme.SurfaceDark
import com.crlsribeiro.reelcine.presentation.theme.Violet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    onNavigateBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Violet)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.movies, key = { it.id }) { movie ->
                    MovieGridCard(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}

@Composable
fun MovieGridCard(movie: Movie, onClick: () -> Unit) {
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
