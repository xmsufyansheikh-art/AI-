package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.MainScreen
import com.example.ui.theme.GulaboAITheme
import com.example.ui.viewmodel.GulaboViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GulaboViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            GulaboAITheme(themeMode = themeMode) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
