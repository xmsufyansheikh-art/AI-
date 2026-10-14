package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.export.FileExportHelper
import com.example.data.export.PdfExportHelper
import com.example.data.model.AIMode
import com.example.data.model.ChatMessage
import com.example.ui.components.VoiceInputBar
import com.example.ui.theme.DarkBlueBorder
import com.example.ui.theme.ElectricSkyBlue
import com.example.ui.theme.GulaboRose
import com.example.ui.theme.MidnightNavy
import com.example.ui.viewmodel.GulaboViewModel

@Composable
fun ChatScreen(
    viewModel: GulaboViewModel,
    onLaunchSpeechRecognizer: () -> Unit,
    onLaunchPhotoPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val activeMode by viewModel.currentMode.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    val currentlySpeakingId by viewModel.speechManager.currentlySpeakingId.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Mode Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AIMode.values().forEach { mode ->
                FilterChip(
                    selected = activeMode == mode,
                    onClick = { viewModel.setAIMode(mode) },
                    label = {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        val icon = when (mode) {
                            AIMode.SUPER_GULABO -> Icons.Default.AutoAwesome
                            AIMode.CODE_ARCHITECT -> Icons.Default.Code
                            AIMode.URDU_SHAYARI -> Icons.Default.Favorite
                            AIMode.BUSINESS_STRATEGY -> Icons.Default.TrendingUp
                            AIMode.POLYGLOT_TRANSLATOR -> Icons.Default.AutoAwesome
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("mode_chip_${mode.name}")
                )
            }
        }

        // Chat message list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("chat_messages_list"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    isSpeaking = currentlySpeakingId == message.id,
                    onSpeak = {
                        viewModel.speechManager.speak(message.content, message.id)
                    },
                    onCopy = {
                        FileExportHelper.copyToClipboard(context, message.content)
                    },
                    onShare = {
                        FileExportHelper.shareText(context, message.content)
                    },
                    onExportPdf = {
                        PdfExportHelper.generateAndSharePdf(
                            context = context,
                            title = "Gulabo AI Response",
                            content = message.content
                        )
                    }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ElectricSkyBlue,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "گلابو اے آئی سوچ رہی ہے... Thinking in all languages...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Quick discovery suggestion pills
            item {
                QuickPromptChips(
                    onSelect = { prompt ->
                        viewModel.sendChatMessage(prompt)
                    }
                )
            }
        }

        // Voice & Text Input Bar
        VoiceInputBar(
            text = inputText,
            onTextChanged = { inputText = it },
            onSend = {
                viewModel.sendChatMessage(inputText)
                inputText = ""
            },
            onMicClick = onLaunchSpeechRecognizer,
            onPickImage = onLaunchPhotoPicker,
            isLoading = isLoading
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onExportPdf: () -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.88f else 0.95f),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!isUser) {
                // Gulabo Avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(ElectricSkyBlue, GulaboRose))
                        )
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MidnightNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌹", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, DarkBlueBorder) else null,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Message content with code block support
                    RenderRichText(message.content)

                    // Action buttons for model replies
                    if (!isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Voice Speak (TTS)
                            IconButton(
                                onClick = onSpeak,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Speak aloud",
                                    tint = if (isSpeaking) GulaboRose else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Copy
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy message",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // PDF Export
                            IconButton(
                                onClick = onExportPdf,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Export to PDF",
                                    tint = ElectricSkyBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Share
                            IconButton(
                                onClick = onShare,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share message",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderRichText(content: String) {
    val parts = content.split("```")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in parts.indices) {
            val part = parts[i]
            if (i % 2 == 1) {
                // Code Block
                val codeLines = part.lines()
                val lang = codeLines.firstOrNull()?.trim() ?: "code"
                val actualCode = if (codeLines.size > 1) codeLines.drop(1).joinToString("\n") else part

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MidnightNavy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBlueBorder, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ElectricSkyBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = actualCode,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            ),
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            } else {
                if (part.isNotBlank()) {
                    Text(
                        text = part.trim(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPromptChips(onSelect: (String) -> Unit) {
    val prompts = listOf(
        "🌹 اردو میں دل کو چھو لینے والی شاعری",
        "💻 Write a Kotlin coroutine flow example",
        "🌐 Translate: السلام علیکم، آپ سے مل کر خوشی ہوئی",
        "🚀 Explain Cloud AI & Quantum Computing simply",
        "📈 Draft a modern business startup plan"
    )

    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = "✨ Quick Suggestions for Gulabo AI:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            prompts.forEach { p ->
                AssistChip(
                    onClick = { onSelect(p) },
                    label = { Text(p, fontSize = 12.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = AssistChipDefaults.assistChipBorder(true, borderColor = DarkBlueBorder)
                )
            }
        }
    }
}
