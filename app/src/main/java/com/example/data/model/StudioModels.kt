package com.example.data.model

data class ImageGenRequest(
    val prompt: String,
    val style: String = "Cinematic Photorealistic",
    val aspectRatio: String = "1:1",
    val negativePrompt: String = "blurry, low quality, distorted"
)

data class VideoScene(
    val sceneNumber: Int,
    val durationSec: Int,
    val title: String,
    val cameraMovement: String,
    val visualDescription: String,
    val lightingAndAtmosphere: String,
    val audioSoundtrackPrompt: String
)

data class VideoProductionPlan(
    val title: String,
    val promptSummary: String,
    val fullAiPrompt: String,
    val motionPreset: String,
    val cameraAngle: String,
    val durationSeconds: Int,
    val scenes: List<VideoScene>
)
