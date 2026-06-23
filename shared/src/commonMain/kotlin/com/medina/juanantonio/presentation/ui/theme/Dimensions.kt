package com.medina.juanantonio.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
data class AppDimensions(
    val spacingXs: androidx.compose.ui.unit.Dp,
    val spacingSm: androidx.compose.ui.unit.Dp,
    val spacingMd: androidx.compose.ui.unit.Dp,
    val spacingLg: androidx.compose.ui.unit.Dp,
    val cornerRadius: androidx.compose.ui.unit.Dp
)

val DefaultDimensions = AppDimensions(
    spacingXs = 4.dp,
    spacingSm = 8.dp,
    spacingMd = 16.dp,
    spacingLg = 24.dp,
    cornerRadius = 12.dp
)