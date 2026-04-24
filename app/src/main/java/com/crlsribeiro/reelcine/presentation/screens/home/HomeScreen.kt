package com.crlsribeiro.reelcine.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.crlsribeiro.reelcine.R
import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.presentation.theme.Gold
import com.crlsribeiro.reelcine.presentation.theme.SurfaceDark
import com.crlsribeiro.reelcine.presentation.theme.Violet

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

    // Memoização das lambdas para evitar recomposição dos filhos
    val onMovieClickHandle = remember(onMovieClick) { { id: Int -> onMovieClick(id) } }
    val onSeeAllHandle = remember(onSeeAllClick) { { category: String -> onSeeAllClick(category) } }

    val trendingRowState = rememberLazyListState()
    val nowPlayingRowState = rememberLazyListState()
    val upcomingRowState = rememberLazyListState()
    val popularRowState = rememberLazyListState()
    val topRatedRowState = rememberLazyListState()

    // Verificação de loading otimizada
    if (uiState.isLoading && uiState.trendingToday.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Violet)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item(key = "hero") {
            uiState.heroMovie?.let { movie ->
                HeroMovieCard(movie = movie, onClick = { onMovieClickHandle(movie.id) })
            }
        }

        // Helper function para manter o código limpo no LazyColumn
        renderMovieSection(
            titleRes = R.string.home_trending,
            movies = uiState.trendingToday,
            state = trendingRowState,
            category = "trending_today",
            onMovieClick = onMovieClickHandle,
            onSeeAllClick = onSeeAllHandle
        )

        renderMovieSection(
            titleRes = R.string.home_now_playing,
            movies = uiState.nowPlaying,
            state = nowPlayingRowState,
            category = "now_playing",
            onMovieClick = onMovieClickHandle,
            onSeeAllClick = onSeeAllHandle
        )

        renderMovieSection(
            titleRes = R.string.home_upcoming,
            movies = uiState.upcoming,
            state = upcomingRowState,
            category = "upcoming",
            onMovieClick = onMovieClickHandle,
            onSeeAllClick = onSeeAllHandle
        )

        renderMovieSection(
            titleRes = R.string.home_popular,
            movies = uiState.popular,
            state = popularRowState,
            category = "popular",
            onMovieClick = onMovieClickHandle,
            onSeeAllClick = onSeeAllHandle
        )

        renderMovieSection(
            titleRes = R.string.home_top_rated,
            movies = uiState.topRated,
            state = topRatedRowState,
            category = "top_rated",
            onMovieClick = onMovieClickHandle,
            onSeeAllClick = onSeeAllHandle
        )
    }
}

// Extensão para organizar o código do LazyColumn
private fun androidx.compose.foundation.lazy.LazyListScope.renderMovieSection(
    titleRes: Int,
    movies: List<Movie>,
    state: LazyListState,
    category: String,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: (String) -> Unit
) {
    if (movies.isNotEmpty()) {
        item(key = category) {
            MovieSection(
                title = stringResource(titleRes),
                movies = movies,
                rowState = state,
                onMovieClick = onMovieClick,
                onSeeAllClick = { onSeeAllClick(category) }
            )
        }
    }
}

@Composable
fun HeroMovieCard(movie: Movie, onClick: () -> Unit) {
    val context = LocalContext.current

    // Otimização: Remember do ImageRequest
    val imageRequest = remember(movie.backdropPath) {
        ImageRequest.Builder(context)
            .data(movie.backdropPath)
            .crossfade(true)
            .size(1080, 720)
            .build()
    }

    Box(modifier = Modifier.fillMaxWidth().height(420.dp).clickable(onClick = onClick)) {
        AsyncImage(
            model = imageRequest,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(heroGradient))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D28D9),
                    contentColor = Color.White
                )
            ) {
                Text(
                    stringResource(R.string.home_watch_trailer),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MovieSection(
    title: String,
    movies: List<Movie>,
    rowState: LazyListState,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_see_all),
                style = MaterialTheme.typography.bodyMedium,
                color = Violet,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            state = rowState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = movies,
                key = { it.id },
                contentType = { "movie_card" } // Ajuda o Compose no reaproveitamento de itens
            ) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    val context = LocalContext.current

    // Otimização: ImageRequest e formatação de texto memoizados
    val imageRequest = remember(movie.posterPath) {
        ImageRequest.Builder(context)
            .data(movie.posterPath)
            .crossfade(true)
            .size(260, 380)
            .build()
    }

    val formattedRating = remember(movie.voteAverage) {
        String.format("%.1f", movie.voteAverage)
    }

    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(130.dp)
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
            )

            // Badge de Rating
            RatingBadge(rating = formattedRating)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            minLines = 2, // Mantém o alinhamento visual se o título for curto
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BoxScope.RatingBadge(rating: String) {
    Row(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(6.dp)
            .background(
                color = ratingBadgeColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "★", color = Gold, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = rating,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}