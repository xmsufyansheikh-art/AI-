package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.export.FileExportHelper
import com.example.data.export.PdfExportHelper
import com.example.data.model.VideoProductionPlan
import com.example.ui.theme.DarkBlueBorder
import com.example.ui.theme.DarkBlueContainer
import com.example.ui.theme.DarkBlueElevated
import com.example.ui.theme.ElectricSkyBlue
import com.example.ui.theme.GulaboRose
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.GulaboViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: GulaboViewModel,
    onLaunchPhotoPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val studioTab by viewModel.studioTab.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Header: Picture Generator vs Video Studio
        PrimaryTabRow(
            selectedTabIndex = studioTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ElectricSkyBlue,
            divider = {}
        ) {
            Tab(
                selected = studioTab == 0,
                onClick = { viewModel.setStudioTab(0) },
                text = { Text("🎨 Picture Generator", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_picture_gen")
            )
            Tab(
                selected = studioTab == 1,
                onClick = { viewModel.setStudioTab(1) },
                text = { Text("🎬 Picture-to-Video", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_video_gen")
            )
        }

        if (studioTab == 0) {
            PictureGeneratorSection(viewModel, onLaunchPhotoPicker)
        } else {
            VideoGeneratorSection(viewModel, onLaunchPhotoPicker)
        }
    }
}

@Composable
fun PictureGeneratorSection(
    viewModel: GulaboViewModel,
    onLaunchPhotoPicker: () -> Unit
) {
    val context = LocalContext.current
    val prompt by viewModel.imagePrompt.collectAsState()
    val selectedStyle by viewModel.selectedImageStyle.collectAsState()
    val generatedBitmap by viewModel.generatedImageBitmap.collectAsState()
    val isGenerating by viewModel.isGeneratingImage.collectAsState()
    val errorMsg by viewModel.imageGenError.collectAsState()

    val styles = listOf(
        "Cinematic Photorealistic",
        "Cyberpunk Neon",
        "3D Anime Render",
        "Digital Concept Art",
        "Fantasy Oil Painting"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Prompt Input
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlueBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Describe the Image You Want to Create:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.setImagePrompt(it) },
                    placeholder = {
                        Text(
                            "e.g. Glowing cybernetic rose in a rainy Tokyo street at midnight, sapphire reflections...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("image_prompt_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = ElectricSkyBlue,
                        unfocusedBorderColor = DarkBlueBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Style Selector Chips
                Text(
                    text = "Select Art Style:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.forEach { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { viewModel.setImageStyle(style) },
                            label = { Text(style, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.generateImage() },
                    enabled = prompt.isNotBlank() && !isGenerating,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_image_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing Image with Gemini 2.5 Flash Image...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Image")
                    }
                }
            }
        }

        // Generated Image Display Card
        if (generatedBitmap != null || errorMsg != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricSkyBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("generated_image_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (generatedBitmap != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                bitmap = generatedBitmap!!.asImageBitmap(),
                                contentDescription = "Generated Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resolution: 1024x1024 • 1K",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row {
                                IconButton(
                                    onClick = { FileExportHelper.shareImage(context, generatedBitmap!!) }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = ElectricSkyBlue)
                                }
                            }
                        }
                    } else if (errorMsg != null) {
                        Text(
                            text = "Note: $errorMsg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Quick Prompt Inspirations
        Text(
            text = "💡 Creative Inspiration Prompts:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        val samplePrompts = listOf(
            "Cybernetic rose flower blooming in neon sapphire crystal rain",
            "Lahore Badshahi Mosque in a futuristic cyberpunk 2099 setting",
            "Majestic golden falcon flying over high-tech desert skyscrapers",
            "Luminous deep-sea bioluminescent jellyfish in dark blue abyss"
        )

        samplePrompts.forEach { p ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setImagePrompt(p)
                    }
                    .border(1.dp, DarkBlueBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = p,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun VideoGeneratorSection(
    viewModel: GulaboViewModel,
    onLaunchPhotoPicker: () -> Unit
) {
    val context = LocalContext.current
    val concept by viewModel.videoConcept.collectAsState()
    val motionPreset by viewModel.videoMotionPreset.collectAsState()
    val cameraAngle by viewModel.videoCameraAngle.collectAsState()
    val duration by viewModel.videoDuration.collectAsState()
    val videoPlan by viewModel.videoPlan.collectAsState()
    val isGenerating by viewModel.isGeneratingVideo.collectAsState()
    val isPlaying by viewModel.isVideoPlaying.collectAsState()
    val activeSceneIndex by viewModel.activeSceneIndex.collectAsState()
    val selectedImageBitmap by viewModel.selectedImageBitmap.collectAsState()

    val motionPresets = listOf(
        "Cinematic Orbit & Zoom",
        "Dynamic Drone Flyover",
        "Horizontal Panoramic Pan",
        "Slow Motion Dramatic Reveal",
        "Hyperlapse Cyberpunk Shift"
    )

    val cameraAngles = listOf(
        "Wide Angle 4K HDR",
        "Close Up Portrait",
        "Low Angle Heroic",
        "FPV Aerial Drone"
    )

    // Animated zoom/pan simulation for interactive video player
    val infiniteTransition = rememberInfiniteTransition(label = "video_sim")
    val animScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Concept Input Card
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
                        text = "Video Concept / Scene Description:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Picture-to-Video Source Indicator
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedImageBitmap != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onLaunchPhotoPicker() }
                    ) {
                        Text(
                            text = if (selectedImageBitmap != null) "📸 Image Attached" else "+ Attach Image",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = concept,
                    onValueChange = { viewModel.setVideoConcept(it) },
                    placeholder = {
                        Text("e.g. Cyber rose blooming in rain with glowing circuit neon reflections")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("video_concept_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = ElectricSkyBlue,
                        unfocusedBorderColor = DarkBlueBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Motion Preset Chips
                Text("Camera Motion Dynamics:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    motionPresets.forEach { preset ->
                        FilterChip(
                            selected = motionPreset == preset,
                            onClick = { viewModel.setVideoMotionPreset(preset) },
                            label = { Text(preset, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Camera Angle Chips
                Text("Lens & Perspective:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cameraAngles.forEach { angle ->
                        FilterChip(
                            selected = cameraAngle == angle,
                            onClick = { viewModel.setVideoCameraAngle(angle) },
                            label = { Text(angle, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.generateVideoPlan() },
                    enabled = concept.isNotBlank() && !isGenerating,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_video_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Directing Cinematic Video Plan with Veo...")
                    } else {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesize Video & Storyboard")
                    }
                }
            }
        }

        // Interactive Video Simulation Player & Scene Storyboard
        if (videoPlan != null) {
            val plan = videoPlan!!
            val activeScene = plan.scenes.getOrNull(activeSceneIndex) ?: plan.scenes.first()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricSkyBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("video_player_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Player Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricSkyBlue
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "4K • 60fps • ${plan.durationSeconds}s",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Video Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(MidnightNavy, DarkBlueContainer)
                                )
                            )
                            .border(1.dp, DarkBlueBorder, RoundedCornerShape(12.dp))
                    ) {
                        // Animated graphic simulation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(if (isPlaying) animScale else 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageBitmap != null) {
                                Image(
                                    bitmap = selectedImageBitmap!!.asImageBitmap(),
                                    contentDescription = "Simulated Video",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_gulabo_icon),
                                    contentDescription = "Gulabo Cinema",
                                    modifier = Modifier.size(110.dp)
                                )
                            }
                        }

                        // Play/Pause Overlay Button
                        IconButton(
                            onClick = { viewModel.toggleVideoPlay() },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(MidnightNavy.copy(alpha = 0.75f))
                                .border(1.5.dp, ElectricSkyBlue, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play simulated video",
                                tint = ElectricSkyBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Active Scene Bar Indicator
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MidnightNavy.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Scene ${activeScene.sceneNumber}/${plan.scenes.size}: ${activeScene.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Scene Details Breakdown
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ElectricSkyBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Camera Trajectory: ${activeScene.cameraMovement}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeScene.visualDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = GulaboRose, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Audio SFX: ${activeScene.audioSoundtrackPrompt}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GulaboRose
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Export Buttons for Video Production Plan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                FileExportHelper.copyToClipboard(context, plan.fullAiPrompt, "Veo Master Prompt")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Veo Prompt", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val docContent = StringBuilder()
                                docContent.append("GULABO AI - CINEMATIC VIDEO SPECIFICATION\n\n")
                                docContent.append("Title: ${plan.title}\n")
                                docContent.append("Duration: ${plan.durationSeconds}s | Angle: ${plan.cameraAngle} | Motion: ${plan.motionPreset}\n\n")
                                docContent.append("MASTER VEO PROMPT:\n${plan.fullAiPrompt}\n\n")
                                docContent.append("SCENE BREAKDOWN:\n")
                                plan.scenes.forEach { s ->
                                    docContent.append("• Scene ${s.sceneNumber} (${s.durationSec}s) - ${s.title}\n")
                                    docContent.append("  Camera: ${s.cameraMovement}\n")
                                    docContent.append("  Visual: ${s.visualDescription}\n")
                                    docContent.append("  Lighting: ${s.lightingAndAtmosphere}\n")
                                    docContent.append("  Audio Prompt: ${s.audioSoundtrackPrompt}\n\n")
                                }
                                PdfExportHelper.generateAndSharePdf(
                                    context = context,
                                    title = plan.title,
                                    content = docContent.toString()
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Storyboard PDF", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
