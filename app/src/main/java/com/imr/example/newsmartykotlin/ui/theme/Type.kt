package com.imr.example.newsmartykotlin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

    val SfProDisplayBlackItalic = FontFamily(
        Font(
            R.font.sfprodisplay_black_italic,
            weight = FontWeight.Black,
            style = FontStyle.Italic
        )
        )

val SfProDisplayBold = FontFamily(
    Font(
        R.font.sfprodisplay_bold,
        weight = FontWeight.Bold
    )
)

val SfProDisplayMedium = FontFamily(
    Font(
        R.font.sfprodisplay_medium,
        weight = FontWeight.Medium
    )
)

val SfProDisplayRegular = FontFamily(
    Font(
        R.font.sfprodisplay_regular,
        weight = FontWeight.Normal
    )
)

val SfProDisplaySemiBoldItalic = FontFamily(
    Font(
        R.font.sfprodisplay_semibold_italic,
        weight = FontWeight.SemiBold,
        style = FontStyle.Italic
    )
)

object AppTypography {

    val Heading = TextStyle(
        fontFamily = SfProDisplayBold,
        fontSize = 28.sp
    )

    val Title = TextStyle(
        fontFamily = SfProDisplayMedium,
        fontSize = 20.sp
    )

    val Body = TextStyle(
        fontFamily = SfProDisplayRegular,
        fontSize = 16.sp
    )

    val ItalicHeading = TextStyle(
        fontFamily = SfProDisplayBlackItalic,
        fontSize = 30.sp
    )

    val SemiBoldItalic = TextStyle(
        fontFamily = SfProDisplaySemiBoldItalic,
        fontSize = 18.sp
    )
}
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
