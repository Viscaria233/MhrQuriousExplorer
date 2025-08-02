package com.haochen.mhrquriousexplorer

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import mhrquriousexplorer.composeapp.generated.resources.Res
import mhrquriousexplorer.composeapp.generated.resources.msyh
import mhrquriousexplorer.composeapp.generated.resources.msyhbd
import mhrquriousexplorer.composeapp.generated.resources.msyhl
import org.jetbrains.compose.resources.Font

@Composable
fun myFontFamily() = FontFamily(
    Font(Res.font.msyhl, weight = FontWeight.Thin),
    Font(Res.font.msyhl, weight = FontWeight.ExtraLight),
    Font(Res.font.msyhl, weight = FontWeight.Light),
    Font(Res.font.msyh, weight = FontWeight.Normal),
    Font(Res.font.msyh, weight = FontWeight.Medium),
    Font(Res.font.msyhbd, weight = FontWeight.SemiBold),
    Font(Res.font.msyhbd, weight = FontWeight.Bold),
    Font(Res.font.msyhbd, weight = FontWeight.ExtraBold),
    Font(Res.font.msyhbd, weight = FontWeight.Black),
)

@Composable
fun myTypography() = Typography().run {
    val fontFamily = myFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}