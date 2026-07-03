package com.imr.example.newsmartykotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.imr.example.newsmartykotlin.core.ads.AdLoadingState
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.navigation.NewSmartyKotlin
import com.imr.example.newsmartykotlin.ui.theme.NewSmartyKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewSmartyKotlinTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isInterstitialShowing by AdLoadingState.isInterstitialShowing.collectAsState()
                val isAdDismissed by AdLoadingState.isAdDismissed.collectAsState()

                LaunchedEffect(currentRoute, isInterstitialShowing, isAdDismissed) {
                    val shouldRestrict = when {
                        isInterstitialShowing -> true
                        isAdDismissed -> true
                        currentRoute == AppRoutes.Splash.route -> true
                        currentRoute == AppRoutes.Premium.route -> true
                        else -> false
                    }
                    MyApp.appOpenManager?.setIsRestricted(shouldRestrict)
                }

                NewSmartyKotlin(
                    navController = navController
                )
            }
        }
    }
}