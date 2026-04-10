package com.carlosribeiro.reelcine.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            // SplashScreen()
        }
        composable(Screen.Login.route) {
            // LoginScreen()
        }
        composable(Screen.Register.route) {
            // RegisterScreen()
        }
        composable(Screen.ForgotPassword.route) {
            // ForgotPasswordScreen()
        }
        composable(Screen.Home.route) {
            // HomeScreen()
        }
        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            // MovieDetailScreen(movieId = movieId)
        }
        composable(Screen.Groups.route) {
            // GroupsScreen()
        }
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
            // GroupDetailScreen(groupId = groupId)
        }
        composable(Screen.Feed.route) {
            // FeedScreen()
        }
        composable(Screen.Profile.route) {
            // ProfileScreen()
        }
    }
}
