package com.imr.example.newsmartykotlin.domain.model

data class LanguageModel(
    val flags: Int,
    val heading: String,
    val languageName: String,
    val languageCode: String,
    val languageCodeLocal: String,
    var isSelected: Boolean = false
)