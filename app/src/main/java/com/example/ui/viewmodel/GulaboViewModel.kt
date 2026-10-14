package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.SpeechManager
import com.example.data.export.FileExportHelper
import com.example.data.export.PdfExportHelper
import com.example.data.model.AIMode
import com.example.data.model.ChatMessage
import com.example.data.model.Language
import com.example.data.model.SupportedLanguages
import com.example.data.model.VideoProductionPlan
import com.example.data.remote.GeminiService
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

enum class AppTab(val title: String, val iconName: String) {
    CHAT("Gulabo AI", "Chat"),
    TRANSLATE("Translator", "Translate"),
    VISION("Vision OCR", "Visibility"),
    STUDIO("AI Studio", "MovieCreation"),
    EXPORT("PDF & Docs", "Description")
}

class GulaboViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiService()
    val speechManager = SpeechManager(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.CHAT)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Theme Mode (Default to DARK per user request for dark blue interface)
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // --- Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _currentMode = MutableStateFlow(AIMode.SUPER_GULABO)
    val currentMode: StateFlow<AIMode> = _currentMode.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- Translator State ---
    private val _sourceLang = MutableStateFlow(SupportedLanguages.getByCode("ur")) // Default Urdu
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(SupportedLanguages.getByCode("en")) // Default English
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _translateInputText = MutableStateFlow("")
    val translateInputText: StateFlow<String> = _translateInputText.asStateFlow()

    private val _translatedResult = MutableStateFlow("")
    val translatedResult: StateFlow<String> = _translatedResult.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    // --- Vision State ---
    private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

    private val _visionAnalysisText = MutableStateFlow("")
    val visionAnalysisText: StateFlow<String> = _visionAnalysisText.asStateFlow()

    private val _isVisionLoading = MutableStateFlow(false)
    val isVisionLoading: StateFlow<Boolean> = _isVisionLoading.asStateFlow()

    // --- AI Studio (Picture & Video) ---
    private val _studioTab = MutableStateFlow(0) // 0: Image, 1: Video
    val studioTab: StateFlow<Int> = _studioTab.asStateFlow()

    private val _imagePrompt = MutableStateFlow("")
    val imagePrompt: StateFlow<String> = _imagePrompt.asStateFlow()

    private val _selectedImageStyle = MutableStateFlow("Cinematic Photorealistic")
    val selectedImageStyle: StateFlow<String> = _selectedImageStyle.asStateFlow()

    private val _generatedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val generatedImageBitmap: StateFlow<Bitmap?> = _generatedImageBitmap.asStateFlow()

    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _imageGenError = MutableStateFlow<String?>(null)
    val imageGenError: StateFlow<String?> = _imageGenError.asStateFlow()

    // Video Studio
    private val _videoConcept = MutableStateFlow("Futuristic cybernetic rose blooming in a neon rain city")
    val videoConcept: StateFlow<String> = _videoConcept.asStateFlow()

    private val _videoMotionPreset = MutableStateFlow("Cinematic Orbit & Zoom")
    val videoMotionPreset: StateFlow<String> = _videoMotionPreset.asStateFlow()

    private val _videoCameraAngle = MutableStateFlow("Wide Angle 4K HDR")
    val videoCameraAngle: StateFlow<String> = _videoCameraAngle.asStateFlow()

    private val _videoDuration = MutableStateFlow(10)
    val videoDuration: StateFlow<Int> = _videoDuration.asStateFlow()

    private val _videoPlan = MutableStateFlow<VideoProductionPlan?>(null)
    val videoPlan: StateFlow<VideoProductionPlan?> = _videoPlan.asStateFlow()

    private val _isGeneratingVideo = MutableStateFlow(false)
    val isGeneratingVideo: StateFlow<Boolean> = _isGeneratingVideo.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    private val _activeSceneIndex = MutableStateFlow(0)
    val activeSceneIndex: StateFlow<Int> = _activeSceneIndex.asStateFlow()

    // --- PDF / Document Export State ---
    private val _docTitle = MutableStateFlow("Gulabo AI Comprehensive Brief")
    val docTitle: StateFlow<String> = _docTitle.asStateFlow()

    private val _docContent = MutableStateFlow("")
    val docContent: StateFlow<String> = _docContent.asStateFlow()

    init {
        // Initial welcome message in Gulabo AI
        _chatMessages.value = listOf(
            ChatMessage(
                role = "model",
                content = "🌹 **خوش آمدید! میں ہوں گلابو اے آئی (Gulabo AI)**\n\nآپ کا جدید ترین کلاؤڈ اور ملٹی لنگوئل معاون۔ میں اردو، ہندی، انگریزی، عربی اور تمام زبانوں میں تیز ترین سوچ بچار، کوڈنگ، شاعری، اور ریئل ٹائم ترجمہ کر سکتی ہوں۔\n\n• 🎙️ **بولیں یا ٹائپ کریں** (Voice & Text)\n• 🌐 **50+ زبانوں میں فوری ترجمہ**\n• 👁️ **تصویر اور دستاویز کا معائنہ**\n• 🎬 **تصویر اور ویڈیو جنریشن اسٹوڈیو**\n• 📄 **ایک کلک پر PDF اور فائل ایکسپورٹ**\n\nآپ مجھ سے کیا کروانا چاہتے ہیں؟",
                mode = AIMode.SUPER_GULABO
            )
        )
        _docContent.value = "Gulabo AI Intelligence Report\n\nKey Capabilities:\n- Omnipotent Multilingual Reasoning\n- Real-Time Live Speech Translation\n- Multimodal Vision and OCR Scanner\n- Picture-to-Video Storyboarding & Synthesis\n- Instant PDF and Document Export"
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
    }

    fun setAIMode(mode: AIMode) {
        _currentMode.value = mode
    }

    // --- Chat Functions ---
    fun sendChatMessage(userText: String, attachedImageBase64: String? = null) {
        if (userText.isBlank() && attachedImageBase64 == null) return

        val userMsg = ChatMessage(
            role = "user",
            content = userText,
            imageBase64 = attachedImageBase64,
            mode = _currentMode.value
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val result = geminiService.generateChatResponse(
                prompt = userText,
                history = _chatMessages.value,
                mode = _currentMode.value,
                imageBase64 = attachedImageBase64
            )

            _isChatLoading.value = false
            result.onSuccess { replyText ->
                val modelMsg = ChatMessage(
                    role = "model",
                    content = replyText,
                    mode = _currentMode.value
                )
                _chatMessages.value = _chatMessages.value + modelMsg
            }.onFailure { err ->
                val errorMsg = ChatMessage(
                    role = "model",
                    content = "⚠️ معذرت، جواب تیار کرنے میں مسئلہ پیش آیا: ${err.message}",
                    mode = _currentMode.value,
                    isError = true
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            }
        }
    }

    fun clearChat() {
        speechManager.stop()
        _chatMessages.value = listOf(
            ChatMessage(
                role = "model",
                content = "🌹 بات چیت صاف کر دی گئی۔ اب آپ نیا سوال پوچھ سکتے ہیں!",
                mode = _currentMode.value
            )
        )
    }

    // --- Translation Functions ---
    fun setSourceLang(lang: Language) {
        _sourceLang.value = lang
    }

    fun setTargetLang(lang: Language) {
        _targetLang.value = lang
    }

    fun swapLanguages() {
        if (_sourceLang.value.code == "auto") return
        val temp = _sourceLang.value
        _sourceLang.value = _targetLang.value
        _targetLang.value = temp
        val tempText = _translateInputText.value
        _translateInputText.value = _translatedResult.value
        _translatedResult.value = tempText
    }

    fun setTranslateInput(text: String) {
        _translateInputText.value = text
    }

    fun performTranslation() {
        val text = _translateInputText.value.trim()
        if (text.isEmpty()) return

        _isTranslating.value = true
        viewModelScope.launch {
            val res = geminiService.translateText(
                text = text,
                fromLanguage = _sourceLang.value.name,
                toLanguage = _targetLang.value.name
            )
            _isTranslating.value = false
            res.onSuccess {
                _translatedResult.value = it
            }.onFailure {
                _translatedResult.value = "Translation Error: ${it.message}"
            }
        }
    }

    // --- Vision Functions ---
    fun setImageFromUri(uri: Uri) {
        try {
            val context = getApplication<Application>()
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            _selectedImageBitmap.value = bitmap
            _visionAnalysisText.value = ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearVisionImage() {
        _selectedImageBitmap.value = null
        _visionAnalysisText.value = ""
    }

    fun analyzeVision(customPrompt: String) {
        val bitmap = _selectedImageBitmap.value ?: return
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        _isVisionLoading.value = true
        viewModelScope.launch {
            val res = geminiService.generateChatResponse(
                prompt = customPrompt,
                mode = AIMode.SUPER_GULABO,
                imageBase64 = base64
            )
            _isVisionLoading.value = false
            res.onSuccess {
                _visionAnalysisText.value = it
            }.onFailure {
                _visionAnalysisText.value = "Error during vision analysis: ${it.message}"
            }
        }
    }

    // --- Studio Functions ---
    fun setStudioTab(index: Int) {
        _studioTab.value = index
    }

    fun setImagePrompt(text: String) {
        _imagePrompt.value = text
    }

    fun setImageStyle(style: String) {
        _selectedImageStyle.value = style
    }

    fun generateImage() {
        val prompt = _imagePrompt.value.trim()
        if (prompt.isEmpty()) return

        _isGeneratingImage.value = true
        _imageGenError.value = null
        viewModelScope.launch {
            val fullPrompt = "$prompt, ${_selectedImageStyle.value}, highly detailed, 8k masterpiece"
            val res = geminiService.generateImage(fullPrompt)
            _isGeneratingImage.value = false
            res.onSuccess {
                _generatedImageBitmap.value = it
            }.onFailure {
                _imageGenError.value = it.message
            }
        }
    }

    fun setVideoConcept(text: String) {
        _videoConcept.value = text
    }

    fun setVideoMotionPreset(preset: String) {
        _videoMotionPreset.value = preset
    }

    fun setVideoCameraAngle(angle: String) {
        _videoCameraAngle.value = angle
    }

    fun setVideoDuration(seconds: Int) {
        _videoDuration.value = seconds
    }

    fun generateVideoPlan() {
        val concept = _videoConcept.value.trim()
        if (concept.isEmpty()) return

        _isGeneratingVideo.value = true
        viewModelScope.launch {
            val res = geminiService.generateVideoProductionPlan(
                conceptPrompt = concept,
                motionPreset = _videoMotionPreset.value,
                cameraAngle = _videoCameraAngle.value,
                durationSeconds = _videoDuration.value,
                hasImage = _selectedImageBitmap.value != null
            )
            _isGeneratingVideo.value = false
            res.onSuccess {
                _videoPlan.value = it
                _activeSceneIndex.value = 0
            }
        }
    }

    fun toggleVideoPlay() {
        if (_videoPlan.value == null) return
        _isVideoPlaying.value = !_isVideoPlaying.value
        if (_isVideoPlaying.value) {
            startSceneAnimationLoop()
        }
    }

    private fun startSceneAnimationLoop() {
        viewModelScope.launch {
            val totalScenes = _videoPlan.value?.scenes?.size ?: 1
            while (_isVideoPlaying.value) {
                delay(2500)
                if (!_isVideoPlaying.value) break
                _activeSceneIndex.value = (_activeSceneIndex.value + 1) % totalScenes
            }
        }
    }

    // --- Document & PDF Export Functions ---
    fun setDocTitle(title: String) {
        _docTitle.value = title
    }

    fun setDocContent(content: String) {
        _docContent.value = content
    }

    fun populateDocFromChat() {
        val sb = StringBuilder()
        sb.append("Gulabo AI Chat Transcript\n\n")
        for (msg in _chatMessages.value) {
            val role = if (msg.role == "user") "User" else "Gulabo AI"
            sb.append("[$role]:\n${msg.content}\n\n")
        }
        _docTitle.value = "Gulabo AI Chat Export"
        _docContent.value = sb.toString()
        _currentTab.value = AppTab.EXPORT
    }

    fun exportCurrentDocToPdf() {
        val context = getApplication<Application>()
        PdfExportHelper.generateAndSharePdf(
            context = context,
            title = _docTitle.value,
            content = _docContent.value
        )
    }

    fun exportCurrentDocToTxt() {
        val context = getApplication<Application>()
        FileExportHelper.exportAsTextFile(
            context = context,
            text = "${_docTitle.value}\n\n${_docContent.value}",
            filenamePrefix = "Gulabo_Doc"
        )
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.shutdown()
    }
}
