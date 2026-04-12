package com.carlosribeiro.reelcine.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.carlosribeiro.reelcine.presentation.screens.auth.ForgotPasswordScreen
import com.carlosribeiro.reelcine.presentation.screens.auth.LoginScreen
import com.carlosribeiro.reelcine.presentation.screens.auth.RegisterScreen
import com.carlosribeiro.reelcine.presentation.screens.feed.FeedScreen
import com.carlosribeiro.reelcine.presentation.screens.groups.GroupsScreen
import com.carlosribeiro.reelcine.presentation.screens.home.HomeScreen
import com.carlosribeiro.reelcine.presentation.screens.moviedetail.MovieDetailScreen
import com.carlosribeiro.reelcine.presentation.screens.movielist.MovieListScreen
import com.carlosribeiro.reelcine.presentation.screens.profile.ProfileScreen
import com.carlosribeiro.reelcine.presentation.screens.search.SearchScreen
import com.carlosribeiro.reelcine.presentation.screens.splash.SplashScreen
import com.carlosribeiro.reelcine.presentation.screens.editprofile.EditProfileScreen
import com.carlosribeiro.reelcine.presentation.screens.watchlist.WatchlistScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { this.inclusive = true } } },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { this.inclusive = true } } }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { this.inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { this.inclusive = true } } }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) },
                onSeeAllClick = { navController.navigate(Screen.MovieList.createRoute(it)) }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) })
        }
        composable(Screen.Watchlist.route) {
            WatchlistScreen(onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) })
        }
        composable(route = Screen.MovieDetail.route, arguments = listOf(navArgument("movieId") { type = NavType.IntType })) {
            MovieDetailScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(route = Screen.MovieList.route, arguments = listOf(navArgument("category") { type = NavType.StringType })) {
            MovieListScreen(
                onNavigateBack = { navController.popBackStack() },
                onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) }
            )
        }
        composable(Screen.Groups.route) {
            GroupsScreen(onGroupClick = { navController.navigate(Screen.GroupDetail.createRoute(it)) })
        }
        composable(route = Screen.GroupDetail.route, arguments = listOf(navArgument("groupId") { type = NavType.StringType })) {}
        composable(Screen.EditProfile.route) {
            EditProfileScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Feed.route) {
            FeedScreen(onMovieClick = { navController.navigate(Screen.MovieDetail.createRoute(it)) })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                onWatchlistClick = { navController.navigate(Screen.Watchlist.route) },
                onSignOut = { navController.navigate(Screen.Login.route) { popUpTo(0) { this.inclusive = true } } },
                
            )
        }
    }
}
