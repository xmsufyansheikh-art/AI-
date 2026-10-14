package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.export.FileExportHelper
import com.example.data.export.PdfExportHelper
import com.example.ui.theme.DarkBlueBorder
import com.example.ui.theme.ElectricSkyBlue
import com.example.ui.theme.GulaboRose
import com.example.ui.viewmodel.GulaboViewModel

@Composable
fun VisionScreen(
    viewModel: GulaboViewModel,
    onLaunchPhotoPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedBitmap by viewModel.selectedImageBitmap.collectAsState()
    val analysisText by viewModel.visionAnalysisText.collectAsState()
    val isLoading by viewModel.isVisionLoading.collectAsState()
    val currentlySpeakingId by viewModel.speechManager.currentlySpeakingId.collectAsState()

    var customPrompt by remember { mutableStateOf("Describe this image in comprehensive detail and extract all visible text.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = ElectricSkyBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Gulabo Vision & OCR Scanner",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Multimodal Image Recognition, OCR, Document & Code Analysis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Image Picker / Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlueBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (selectedBitmap == null) {
                // Empty state to upload photo
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLaunchPhotoPicker() }
                        .padding(28.dp)
                        .testTag("upload_image_box"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Pick Image",
                        tint = ElectricSkyBlue,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Select or Take a Photo to Scan",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Supports homework, bills, code screenshots, handwritten Urdu/English, diagrams",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            } else {
                // Image Preview with Clear button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.matchParentSize()
                    )
                    IconButton(
                        onClick = { viewModel.clearVisionImage() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove photo",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Vision Presets Chips
        Text(
            text = "⚡ Instant Vision Tasks:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        val visionPresets = listOf(
            "🔍 Comprehensive Image Analysis",
            "📝 Extract Text & OCR Transcript",
            "📐 Solve Math Equation in Image",
            "🌹 اس تصویر کو مکمل اردو میں سمجھائیں",
            "💻 Transcribe & Debug Code Screenshot"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            visionPresets.forEach { preset ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable {
                            customPrompt = preset
                            if (selectedBitmap != null) {
                                viewModel.analyzeVision(preset)
                            }
                        }
                        .border(1.dp, DarkBlueBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Custom Prompt Input
        OutlinedTextField(
            value = customPrompt,
            onValueChange = { customPrompt = it },
            label = { Text("What should Gulabo look for in this image?") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vision_prompt_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = ElectricSkyBlue,
                unfocusedBorderColor = DarkBlueBorder
            )
        )

        // Run Analysis Button
        Button(
            onClick = { viewModel.analyzeVision(customPrompt) },
            enabled = selectedBitmap != null && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_vision_analysis_btn")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing Image with Multimodal AI...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze Image with Gulabo Vision")
            }
        }

        // Analysis Output Card
        AnimatedVisibility(visible = analysisText.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricSkyBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("vision_output_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vision Analysis Result:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElectricSkyBlue
                        )
                        Row {
                            // TTS Speak
                            IconButton(
                                onClick = {
                                    viewModel.speechManager.speak(
                                        text = analysisText,
                                        messageId = "vision_${System.currentTimeMillis()}"
                                    )
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentlySpeakingId?.startsWith("vision_") == true) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Speak analysis",
                                    tint = if (currentlySpeakingId?.startsWith("vision_") == true) GulaboRose else ElectricSkyBlue
                                )
                            }
                            // Copy
                            IconButton(
                                onClick = { FileExportHelper.copyToClipboard(context, analysisText) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            // Export to PDF
                            IconButton(
                                onClick = {
                                    PdfExportHelper.generateAndSharePdf(
                                        context = context,
                                        title = "Gulabo AI Vision OCR Analysis",
                                        content = "Prompt:\n$customPrompt\n\nResult:\n$analysisText"
                                    )
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = ElectricSkyBlue)
                            }
                            // Share
                            IconButton(
                                onClick = { FileExportHelper.shareText(context, analysisText) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = analysisText,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
