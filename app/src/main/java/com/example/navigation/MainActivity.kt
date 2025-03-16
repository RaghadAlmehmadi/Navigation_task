package com.example.navigation

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "first_screen"
    var text by remember { mutableStateOf("") }

    val title = when (currentRoute) {
        "first_screen" -> "First Screen"
        "second_screen/{enteredText}" -> "Second Screen"
        "third_screen" -> "Third Screen"
        else -> "Navigation App"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (currentRoute != "first_screen") {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Button"
                            )
                        }
                    }
                },
                actions = {
                    if (currentRoute == "first_screen") {
                        TextButton(onClick = {
                            val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
                            navController.navigate("second_screen/$encodedText")
                        }) {
                            Text("Save")
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        Navigation(navController, innerPadding, text, onTextChange = { text = it })
    }
}

@Composable
fun Navigation(navController: NavHostController, innerPadding: PaddingValues, text: String, onTextChange: (String) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "first_screen"
    ) {
        composable("first_screen") { FirstScreen(navController, text, onTextChange) }
        composable("second_screen/{enteredText}") { backStackEntry ->
            val enteredText = backStackEntry.arguments?.getString("enteredText") ?: "No text entered"
            Secondscreen(navController, enteredText)
        }
        composable("third_screen") { Thirdscreen(navController) }
    }
}
