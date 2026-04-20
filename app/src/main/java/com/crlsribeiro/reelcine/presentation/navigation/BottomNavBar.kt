package com.crlsribeiro.reelcine.presentation.navigation
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.crlsribeiro.reelcine.R
import com.crlsribeiro.reelcine.presentation.theme.Violet

data class BottomNavItem(val labelRes: Int, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    BottomNavItem(R.string.nav_home, Icons.Default.Home, Screen.Home.route),
    BottomNavItem(R.string.nav_search, Icons.Default.Search, Screen.Search.route),
    BottomNavItem(R.string.nav_groups, Icons.Default.Group, Screen.Groups.route),
    BottomNavItem(R.string.nav_feed, Icons.Default.RssFeed, Screen.Feed.route),
    BottomNavItem(R.string.nav_profile, Icons.Default.Person, Screen.Profile.route)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        bottomNavItems.forEach { item ->
            val label = stringResource(id = item.labelRes)
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Violet,
                    selectedTextColor = Violet,
                    indicatorColor = Violet.copy(alpha = 0.2f)
                )
            )
        }
    }
}
