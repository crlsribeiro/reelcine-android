package com.carlosribeiro.reelcine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.carlosribeiro.reelcine.presentation.navigation.NavGraph
import com.carlosribeiro.reelcine.presentation.theme.ReelCineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReelCineTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
