package com.imr.example.newsmartykotlin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.TextFontOption

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
// ---------- MONTSERRAT ----------

val MontserratRegular = FontFamily(
    Font(
        R.font.montserrat_regular,
        weight = FontWeight.Normal
    )
)

val MontserratMedium = FontFamily(
    Font(
        R.font.montserrat_medium,
        weight = FontWeight.Medium
    )
)

val MontserratBold = FontFamily(
    Font(
        R.font.montserrat_bold,
        weight = FontWeight.Bold
    )
)

// ---------- CUSTOM FONTS ----------

val Font3 = FontFamily(Font(R.font.poppins_regular))
val Font4 = FontFamily(Font(R.font.poppins_medium))
val Font5 = FontFamily(Font(R.font.poppins_bold))
val Font6 = FontFamily(Font(R.font.font6))
val Font25 = FontFamily(Font(R.font.font25))
val Font26 = FontFamily(Font(R.font.font26))
val Font33 = FontFamily(Font(R.font.font33))
val Font34 = FontFamily(Font(R.font.font34))
val Font36 = FontFamily(Font(R.font.font36))
val Font38 = FontFamily(Font(R.font.font38))
val Font39 = FontFamily(Font(R.font.font39))
val Font40 = FontFamily(Font(R.font.font40))
val Font43 = FontFamily(Font(R.font.font43))
val Font44 = FontFamily(Font(R.font.font44))
val TextFonts = listOf(

    TextFontOption(
        id = "sf_pro_bold",
        title = "Default",
        fontFamily = SfProDisplayBold
    ),

    TextFontOption(
        id = "sf_pro_regular",
        title = "Font",
        fontFamily = SfProDisplayRegular
    ),

    TextFontOption(
        id = "sf_pro_medium",
        title = "Font",
        fontFamily = SfProDisplayMedium
    ),

    TextFontOption(
        id = "sf_pro_black_italic",
        title = "Font",
        fontFamily = SfProDisplayBlackItalic
    ),

    TextFontOption(
        id = "sf_pro_semibold_italic",
        title = "Font",
        fontFamily = SfProDisplaySemiBoldItalic
    ),

    TextFontOption(
        id = "montserrat_regular",
        title = "Font",
        fontFamily = MontserratRegular
    ),

    TextFontOption(
        id = "montserrat_medium",
        title = "Font",
        fontFamily = MontserratMedium
    ),

    TextFontOption(
        id = "montserrat_bold",
        title = "Font",
        fontFamily = MontserratBold
    ),

    TextFontOption(
        id = "font3",
        title = "Font",
        fontFamily = Font3
    ),

    TextFontOption(
        id = "font4",
        title = "Font",
        fontFamily = Font4
    ),

    TextFontOption(
        id = "font5",
        title = "Font",
        fontFamily = Font5
    ),

    TextFontOption(
        id = "font6",
        title = "Font",
        fontFamily = Font6
    ),

    TextFontOption(
        id = "font25",
        title = "Font",
        fontFamily = Font25
    ),

    TextFontOption(
        id = "font26",
        title = "Font",
        fontFamily = Font26
    ),

    TextFontOption(
        id = "font33",
        title = "Font",
        fontFamily = Font33
    ),

    TextFontOption(
        id = "font34",
        title = "Font",
        fontFamily = Font34
    ),

    TextFontOption(
        id = "font36",
        title = "Font",
        fontFamily = Font36
    ),

    TextFontOption(
        id = "font38",
        title = "Font 38",
        fontFamily = Font38
    ),

    TextFontOption(
        id = "font39",
        title = "Font",
        fontFamily = Font39
    ),

    TextFontOption(
        id = "font40",
        title = "Font",
        fontFamily = Font40
    ),

    TextFontOption(
        id = "font43",
        title = "Font",
        fontFamily = Font43
    ),

    TextFontOption(
        id = "font44",
        title = "Font",
        fontFamily = Font44
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
