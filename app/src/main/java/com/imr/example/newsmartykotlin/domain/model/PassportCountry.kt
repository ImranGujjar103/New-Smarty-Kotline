package com.imr.example.newsmartykotlin.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class DocumentType {
    ALL,
    PASSPORT,
    VISA,
    STANDARD
}
data class PassportCountry(
    val id: String,
    @StringRes val nameRes: Int,
    val passportSizeMm: String,
    val passportSizeInch: String,
    val passportPixel: String,
    val visaSizeMm: String,
    val visaSizeInch: String,
    val visaPixel: String,
    val standardSizeMm: String,
    val standardSizeInch: String,
    val standardPixel: String,
    val background: String,
    @DrawableRes val flagRes: Int
)
fun PassportCountry.getSizeMm(type: DocumentType): String {
    return when (type) {
        DocumentType.ALL,
        DocumentType.PASSPORT -> passportSizeMm
        DocumentType.VISA -> visaSizeMm
        DocumentType.STANDARD -> standardSizeMm
    }
}

fun PassportCountry.getSizeInch(type: DocumentType): String {
    return when (type) {
        DocumentType.ALL,
        DocumentType.PASSPORT -> passportSizeInch
        DocumentType.VISA -> visaSizeInch
        DocumentType.STANDARD -> standardSizeInch
    }
}

fun PassportCountry.getPixel(type: DocumentType): String {
    return when (type) {
        DocumentType.ALL,
        DocumentType.PASSPORT -> passportPixel
        DocumentType.VISA -> visaPixel
        DocumentType.STANDARD -> standardPixel
    }
}