package com.stephen.aiassistant.ui.theme

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.cooper_black
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

val defaultText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val titleFirstText = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Left,
)

val titleSecondText = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val titleThirdText = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val infoText = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Left,
)

val calorieInfoText = TextStyle(
    fontSize = 26.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
)

@Composable
fun CooperFontFamily() = FontFamily(
    Font(Res.font.cooper_black, weight = FontWeight.Bold),
)
