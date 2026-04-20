package com.carlosribeiro.reelcine.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.carlosribeiro.reelcine.R
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.presentation.theme.Gold
import com.carlosribeiro.reelcine.presentation.theme.SurfaceDark
import com.carlosribeiro.reelcine.presentation.theme.Violet

private val heroGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
)
private val ratingBadgeColor = Color.Black.copy(alpha = 0.7f)

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Violet)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item(key = "hero") {
            uiState.heroMovie?.let { movie ->
                HeroMovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
        item(key = "trending_today") {
            MovieSection(title = stringResource(R.string.home_trending), movies = uiState.trendingToday, onMovieClick = onMovieClick, onSeeAllClick = { onSeeAllClick("trending_today") })
        }
        item(key = "now_playing") {
            MovieSection(title = stringResource(R.string.home_now_playing), movies = uiState.nowPlaying, onMovieClick = onMovieClick, onSeeAllClick = { onSeeAllClick("now_playing") })
        }
        item(key = "upcoming") {
            MovieSection(title = stringResource(R.string.home_upcoming), movies = uiState.upcoming, onMovieClick = onMovieClick, onSeeAllClick = { onSeeAllClick("upcoming") })
        }
        item(key = "popular") {
            MovieSection(title = stringResource(R.string.home_popular), movies = uiState.popular, onMovieClick = onMovieClick, onSeeAllClick = { onSeeAllClick("popular") })
        }
        item(key = "top_rated") {
            MovieSection(title = stringResource(R.string.home_top_rated), movies = uiState.topRated, onMovieClick = onMovieClick, onSeeAllClick = { onSeeAllClick("top_rated") })
        }
    }
}

@Composable
fun HeroMovieCard(movie: Movie, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageRequest = remember(movie.backdropPath) {
        ImageRequest.Builder(context).data(movie.backdropPath).crossfade(false)
            .memoryCacheKey(movie.backdropPath).diskCacheKey(movie.backdropPath).build()
    }
    Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(heroGradient))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D28D9),
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.home_watch_trailer), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MovieSection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit
) {
    if (movies.isEmpty()) return
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = stringResource(R.string.home_see_all),
                style = MaterialTheme.typography.bodyMedium,
                color = Violet,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageRequest = remember(movie.posterPath) {
        ImageRequest.Builder(context).data(movie.posterPath).crossfade(false)
            .memoryCacheKey(movie.posterPath).diskCacheKey(movie.posterPath).size(260, 380).build()
    }
    Column(modifier = Modifier.width(130.dp).clickable(onClick = onClick)) {
        Box {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(130.dp).height(190.dp)
                    .clip(RoundedCornerShape(12.dp)).background(SurfaceDark)
            )
            Row(
                modifier = Modifier.align(Alignment.TopStart).padding(6.dp)
                    .background(color = ratingBadgeColor, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "★", color = Gold, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = String.format("%.1f", movie.voteAverage), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = movie.title, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
    }
}
