package com.carlosribeiro.reelcine.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object Home : Screen("home")
    data object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    data object MovieList : Screen("movie_list/{category}") {
        fun createRoute(category: String) = "movie_list/$category"
    }
    data object Groups : Screen("groups")
    data object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: String) = "group_detail/$groupId"
    }
    data object Feed : Screen("feed")
    data object Profile : Screen("profile")
}
