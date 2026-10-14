package com.example.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GulaboTopAppBar
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ExportDocScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.TranslateScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.DarkBlueBorder
import com.example.ui.theme.ElectricSkyBlue
import com.example.ui.theme.GulaboRose
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.GulaboViewModel

@Composable
fun MainScreen(
    viewModel: GulaboViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isSpeaking by viewModel.speechManager.isSpeakingFlow.collectAsState()

    // ActivityResultLauncher for Voice Speech-To-Text (Urdu, English, Hindi, Arabic, all languages)
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                when (currentTab) {
                    AppTab.CHAT -> viewModel.sendChatMessage(spokenText)
                    AppTab.TRANSLATE -> {
                        viewModel.setTranslateInput(spokenText)
                        viewModel.performTranslation()
                    }
                    AppTab.VISION -> viewModel.analyzeVision(spokenText)
                    AppTab.STUDIO -> viewModel.setImagePrompt(spokenText)
                    AppTab.EXPORT -> viewModel.setDocContent(spokenText)
                }
                Toast.makeText(context, "Voice input received!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val launchSpeechRecognizer: () -> Unit = {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Gulabo AI in any language (Urdu, English, Hindi...)")
            }
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Speech recognition unavailable: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Zero-Permission Android Photo Picker for Google Play Policy compliance
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setImageFromUri(uri)
            if (currentTab != AppTab.VISION && currentTab != AppTab.STUDIO) {
                viewModel.setTab(AppTab.VISION)
            }
            Toast.makeText(context, "Image loaded for Gulabo Vision", Toast.LENGTH_SHORT).show()
        }
    }

    val launchPhotoPicker: () -> Unit = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GulaboTopAppBar(
                themeMode = themeMode,
                isSpeaking = isSpeaking,
                onToggleTheme = { viewModel.toggleTheme() },
                onStopSpeech = { viewModel.speechManager.stop() },
                onClearChat = { viewModel.clearChat() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .border(width = 0.5.dp, color = DarkBlueBorder)
                    .testTag("bottom_nav_bar")
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val icon: ImageVector = when (tab) {
                        AppTab.CHAT -> Icons.AutoMirrored.Filled.Chat
                        AppTab.TRANSLATE -> Icons.Default.Translate
                        AppTab.VISION -> Icons.Default.Visibility
                        AppTab.STUDIO -> Icons.Default.MovieCreation
                        AppTab.EXPORT -> Icons.Default.Description
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricSkyBlue,
                            selectedTextColor = ElectricSkyBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.CHAT -> ChatScreen(
                    viewModel = viewModel,
                    onLaunchSpeechRecognizer = launchSpeechRecognizer,
                    onLaunchPhotoPicker = launchPhotoPicker
                )
                AppTab.TRANSLATE -> TranslateScreen(
                    viewModel = viewModel,
                    onLaunchSpeechRecognizer = launchSpeechRecognizer
                )
                AppTab.VISION -> VisionScreen(
                    viewModel = viewModel,
                    onLaunchPhotoPicker = launchPhotoPicker
                )
                AppTab.STUDIO -> StudioScreen(
                    viewModel = viewModel,
                    onLaunchPhotoPicker = launchPhotoPicker
                )
                AppTab.EXPORT -> ExportDocScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
