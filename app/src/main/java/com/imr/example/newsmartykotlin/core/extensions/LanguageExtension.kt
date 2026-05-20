package com.imr.example.newsmartykotlin.core.extensions

import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.LanguageModel


fun getLocalizationList(): ArrayList<LanguageModel> {
    return arrayListOf(
        LanguageModel(R.drawable.english_flag, "English", "English", "en", "English"),
        LanguageModel(R.drawable.hindi_flag, "Hindi", "Hindi", "hi", "हिन्दी"),
        LanguageModel(R.drawable.turkish_flag, "", "Turkish", "tr", "Türkçe"),
        LanguageModel(R.drawable.portuguese_flag, "", "Portuguese", "pt", "Português"),
        LanguageModel(R.drawable.spainish_flag, "", "Spanish", "es", "Español"),
        LanguageModel(R.drawable.saudia_flag, "", "Arabic", "ar", "العربية"),
        LanguageModel(R.drawable.vietnamese_flag, "", "Vietnamese", "vi", "Tiếng Việt"),
        LanguageModel(R.drawable.french_flag, "", "French", "fr", "Français"),
        LanguageModel(R.drawable.german_flag, "", "German", "de", "Deutsch"),
        LanguageModel(R.drawable.japanese_flag, "", "Japanese", "ja", "日本語"),
        LanguageModel(R.drawable.korean_flag, "", "Korean", "ko", "한국어"),
        LanguageModel(R.drawable.malaysia_flag, "", "Malay", "ms", "Malay"),
        LanguageModel(R.drawable.russian_flag, "", "Russian", "ru", "русский"),
        LanguageModel(R.drawable.thai_flag, "", "Thai", "th", "ไทย")
    )
}
