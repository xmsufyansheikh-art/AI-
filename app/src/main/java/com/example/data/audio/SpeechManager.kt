package com.example.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeakingFlow = MutableStateFlow(false)
    val isSpeakingFlow: StateFlow<Boolean> = _isSpeakingFlow

    private val _currentlySpeakingId = MutableStateFlow<String?>(null)
    val currentlySpeakingId: StateFlow<String?> = _currentlySpeakingId

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.getDefault()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeakingFlow.value = true
                    _currentlySpeakingId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeakingFlow.value = false
                    _currentlySpeakingId.value = null
                }

                override fun onError(utteranceId: String?) {
                    _isSpeakingFlow.value = false
                    _currentlySpeakingId.value = null
                }
            })
        } else {
            Log.e("SpeechManager", "TTS initialization failed")
        }
    }

    fun speak(text: String, messageId: String = "msg_${System.currentTimeMillis()}", languageCode: String? = null) {
        if (!isInitialized || tts == null) return

        if (_isSpeakingFlow.value && _currentlySpeakingId.value == messageId) {
            stop()
            return
        }

        stop()

        try {
            if (!languageCode.isNullOrBlank()) {
                val locale = when (languageCode.lowercase()) {
                    "ur" -> Locale.forLanguageTag("ur-PK")
                    "hi" -> Locale.forLanguageTag("hi-IN")
                    "ar" -> Locale.forLanguageTag("ar-SA")
                    "es" -> Locale.forLanguageTag("es-ES")
                    "fr" -> Locale.forLanguageTag("fr-FR")
                    "de" -> Locale.forLanguageTag("de-DE")
                    "zh" -> Locale.forLanguageTag("zh-CN")
                    "ja" -> Locale.forLanguageTag("ja-JP")
                    "ko" -> Locale.forLanguageTag("ko-KR")
                    "ru" -> Locale.forLanguageTag("ru-RU")
                    "tr" -> Locale.forLanguageTag("tr-TR")
                    else -> Locale.ENGLISH
                }
                val check = tts?.isLanguageAvailable(locale)
                if (check != TextToSpeech.LANG_NOT_SUPPORTED && check != TextToSpeech.LANG_MISSING_DATA) {
                    tts?.language = locale
                }
            }
        } catch (e: Exception) {
            Log.e("SpeechManager", "Could not set language", e)
        }

        // Clean markdown syntax for speech
        val cleaned = text
            .replace(Regex("```[a-zA-Z]*"), "")
            .replace("```", "")
            .replace("**", "")
            .replace("*", "")
            .replace("#", "")
            .replace("`", "")
            .take(1500) // comfortable speech length

        tts?.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, messageId)
    }

    fun stop() {
        tts?.stop()
        _isSpeakingFlow.value = false
        _currentlySpeakingId.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
