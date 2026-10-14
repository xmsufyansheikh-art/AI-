package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.export.FileExportHelper
import com.example.data.export.PdfExportHelper
import com.example.data.model.Language
import com.example.data.model.SupportedLanguages
import com.example.ui.theme.DarkBlueBorder
import com.example.ui.theme.ElectricSkyBlue
import com.example.ui.theme.GulaboRose
import com.example.ui.viewmodel.GulaboViewModel

@Composable
fun TranslateScreen(
    viewModel: GulaboViewModel,
    onLaunchSpeechRecognizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val inputText by viewModel.translateInputText.collectAsState()
    val translatedResult by viewModel.translatedResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val currentlySpeakingId by viewModel.speechManager.currentlySpeakingId.collectAsState()

    var showSourceMenu by remember { mutableStateOf(false) }
    var showTargetMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = ElectricSkyBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Gulabo Real-Time Translator",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "50+ Languages with Voice, Speech & Instant Export",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Language Selectors Row with Swap Button
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlueBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source Language Picker
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSourceMenu = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("source_lang_picker")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sourceLang.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    sourceLang.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    sourceLang.nativeName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showSourceMenu,
                        onDismissRequest = { showSourceMenu = false }
                    ) {
                        SupportedLanguages.list.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text("${lang.flagEmoji} ${lang.name} (${lang.nativeName})") },
                                onClick = {
                                    viewModel.setSourceLang(lang)
                                    showSourceMenu = false
                                }
                            )
                        }
                    }
                }

                // Swap Button
                IconButton(
                    onClick = { viewModel.swapLanguages() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("swap_languages_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Languages",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Target Language Picker
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTargetMenu = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("target_lang_picker")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(targetLang.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    targetLang.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    targetLang.nativeName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showTargetMenu,
                        onDismissRequest = { showTargetMenu = false }
                    ) {
                        SupportedLanguages.list.filter { it.code != "auto" }.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text("${lang.flagEmoji} ${lang.name} (${lang.nativeName})") },
                                onClick = {
                                    viewModel.setTargetLang(lang)
                                    showTargetMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Input Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlueBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enter text (${sourceLang.name}):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row {
                        IconButton(
                            onClick = onLaunchSpeechRecognizer,
                            modifier = Modifier.size(32.dp).testTag("translate_mic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Speak into translation",
                                tint = ElectricSkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (inputText.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.setTranslateInput("") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear input",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.setTranslateInput(it) },
                    placeholder = {
                        Text(
                            "Type or speak in ${sourceLang.name}... (e.g. اردو، انگریزی، عربی)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("translate_input_text"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = ElectricSkyBlue,
                        unfocusedBorderColor = DarkBlueBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.performTranslation() },
                    enabled = inputText.isNotBlank() && !isTranslating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("execute_translate_btn")
                ) {
                    if (isTranslating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translating...")
                    } else {
                        Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translate to ${targetLang.name}")
                    }
                }
            }
        }

        // Output Result Card
        AnimatedVisibility(visible = translatedResult.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricSkyBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("translated_result_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Translated (${targetLang.name}):",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElectricSkyBlue
                        )
                        Row {
                            // TTS Voice speak
                            IconButton(
                                onClick = {
                                    viewModel.speechManager.speak(
                                        text = translatedResult,
                                        messageId = "trans_${System.currentTimeMillis()}",
                                        languageCode = targetLang.code
                                    )
                                },
                                modifier = Modifier.size(34.dp).testTag("speak_translation_btn")
                            ) {
                                Icon(
                                    imageVector = if (currentlySpeakingId?.startsWith("trans_") == true) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Speak translation",
                                    tint = if (currentlySpeakingId?.startsWith("trans_") == true) GulaboRose else ElectricSkyBlue
                                )
                            }
                            // Copy
                            IconButton(
                                onClick = { FileExportHelper.copyToClipboard(context, translatedResult) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            // Export to PDF
                            IconButton(
                                onClick = {
                                    PdfExportHelper.generateAndSharePdf(
                                        context = context,
                                        title = "Gulabo AI Translation (${sourceLang.name} to ${targetLang.name})",
                                        content = "Original (${sourceLang.name}):\n$inputText\n\nTranslation (${targetLang.name}):\n$translatedResult"
                                    )
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = ElectricSkyBlue)
                            }
                            // Share
                            IconButton(
                                onClick = { FileExportHelper.shareText(context, translatedResult) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = translatedResult,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Quick Phrasebook for Travel & Conversation
        Text(
            text = "⚡ Instant Multilingual Phrases:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        val quickPhrases = listOf(
            "السلام علیکم، آپ کیسے ہیں؟",
            "Can you help me find directions?",
            "How much does this cost?",
            "Thank you very much for your help!",
            "I am learning new languages with Gulabo AI"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPhrases.forEach { phrase ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable {
                            viewModel.setTranslateInput(phrase)
                            viewModel.performTranslation()
                        }
                        .border(1.dp, DarkBlueBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = phrase,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
