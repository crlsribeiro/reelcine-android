package com.carlosribeiro.reelcine.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.carlosribeiro.reelcine.presentation.theme.Violet

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
    BottomNavItem("Buscar", Icons.Default.Search, Screen.Search.route),
    BottomNavItem("Grupos", Icons.Default.Group, Screen.Groups.route),
    BottomNavItem("Feed", Icons.Default.RssFeed, Screen.Feed.route),
    BottomNavItem("Perfil", Icons.Default.Person, Screen.Profile.route)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Violet,
                    selectedTextColor = Violet,
                    indicatorColor = Violet.copy(alpha = 0.2f)
                )
            )
        }
    }
}
