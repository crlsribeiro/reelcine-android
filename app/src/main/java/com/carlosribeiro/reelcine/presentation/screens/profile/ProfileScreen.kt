package com.carlosribeiro.reelcine.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlosribeiro.reelcine.presentation.theme.Violet

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) onSignOut()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = CircleShape,
            color = Violet.copy(alpha = 0.2f),
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.user?.name?.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Violet
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiState.user?.name ?: "Usuário",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = uiState.user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(label = "Filmes", value = "0")
            StatCard(label = "Críticas", value = "0")
            StatCard(label = "Grupos", value = "0")
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfileMenuItem(
            icon = { Icon(Icons.Default.Bookmarks, contentDescription = null, tint = Violet) },
            title = "Minha Watchlist",
            onClick = onWatchlistClick
        )
        ProfileMenuItem(
            icon = { Icon(Icons.Default.History, contentDescription = null, tint = Violet) },
            title = "Histórico de Atividade",
            onClick = onWatchlistClick
        )
        ProfileMenuItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Violet) },
            title = "Editar Perfil",
            onClick = onWatchlistClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.signOut() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair da conta")
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Violet)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ProfileMenuItem(icon: @Composable () -> Unit, title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(text = ">", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
