package com.example.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String
)

object SupportedLanguages {
    val list = listOf(
        Language("auto", "Auto Detect", "خودکار شناخت", "🌐"),
        Language("ur", "Urdu", "اردو", "🇵🇰"),
        Language("en", "English", "English", "🇬🇧"),
        Language("hi", "Hindi", "हिन्दी", "🇮🇳"),
        Language("ar", "Arabic", "العربية", "🇸🇦"),
        Language("es", "Spanish", "Español", "🇪🇸"),
        Language("fr", "French", "Français", "🇫🇷"),
        Language("de", "German", "Deutsch", "🇩🇪"),
        Language("zh", "Chinese", "中文", "🇨🇳"),
        Language("ja", "Japanese", "日本語", "🇯🇵"),
        Language("ko", "Korean", "한국어", "🇰🇷"),
        Language("ru", "Russian", "Русский", "🇷🇺"),
        Language("tr", "Turkish", "Türkçe", "🇹🇷"),
        Language("fa", "Persian", "فارسی", "🇮🇷"),
        Language("ps", "Pashto", "پښتو", "🇦🇫"),
        Language("pa", "Punjabi", "پنجابی / ਪੰਜਾਬੀ", "🇵🇰"),
        Language("bn", "Bengali", "বাংলা", "🇧🇩"),
        Language("pt", "Portuguese", "Português", "🇵🇹"),
        Language("it", "Italian", "Italiano", "🇮🇹"),
        Language("id", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
        Language("ms", "Malay", "Bahasa Melayu", "🇲🇾"),
        Language("nl", "Dutch", "Nederlands", "🇳🇱"),
        Language("sv", "Swedish", "Svenska", "🇸🇪"),
        Language("vi", "Vietnamese", "Tiếng Việt", "🇻🇳"),
        Language("th", "Thai", "ไทย", "🇹🇭")
    )

    fun getByCode(code: String): Language {
        return list.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: list[1]
    }
}
