package com.imr.example.newsmartykotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.imr.example.newsmartykotlin.presentation.navigation.NewSmartyKotlin
import com.imr.example.newsmartykotlin.ui.theme.NewSmartyKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewSmartyKotlinTheme {
                val navController = rememberNavController()
                NewSmartyKotlin(
                    navController = navController
                )
            }
        }
    }
}