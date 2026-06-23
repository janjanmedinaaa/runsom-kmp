package com.medina.juanantonio.presentation.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowDown
import compose.icons.feathericons.Download
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SlideToActionButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    enabled: Boolean = true,
    loadingDurationMs: Long = 500,
    borderColor: Color = Color.White,
    backgroundColor: Color = Color.Transparent,
    knobColor: Color = Color.White,
    loadingSpinnerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    onCompleted: suspend () -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val knobSize = 56.dp
    val knobSizePx = with(density) { knobSize.toPx() }

    var loading by remember { mutableStateOf(false) }

    val offsetX = remember { Animatable(0f) }

    val maxOffset = remember(size, knobSizePx) {
        (size.width - knobSizePx - 32.dp.value).coerceAtLeast(0f)
    }

    val actualBorderColor =
        if (enabled) borderColor else Color.Gray.copy(alpha = 0.5f)

    val actualBackgroundColor =
        if (enabled) backgroundColor else Color.Gray.copy(alpha = 0.15f)

    val actualKnobColor =
        if (enabled) knobColor else Color.LightGray

    val actualContentColor =
        if (enabled) contentColor else Color.Gray

    fun resetSlider() {
        scope.launch {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            )
        }
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .onSizeChanged { size = it }
            .clip(RoundedCornerShape(999.dp))
            .background(actualBackgroundColor)
            .border(
                width = 3.dp,
                color = actualBorderColor,
                shape = RoundedCornerShape(999.dp)
            )
    ) {

        Text(
            text = text,
            color = actualContentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 72.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .padding(8.dp)
                .offset {
                    IntOffset(
                        x = offsetX.value.roundToInt(),
                        y = 0
                    )
                }
                .size(knobSize)
                .clip(CircleShape)
                .background(actualKnobColor)
                .pointerInput(enabled, loading, maxOffset) {
                    if (!enabled || loading) {
                        return@pointerInput
                    }

                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value >= maxOffset * 0.9f) {
                                    offsetX.animateTo(
                                        targetValue = maxOffset,
                                        animationSpec = tween(
                                            durationMillis = 150,
                                            easing = FastOutSlowInEasing
                                        )
                                    )

                                    loading = true

                                    try {
                                        onCompleted()
                                        delay(loadingDurationMs)
                                    } finally {
                                        loading = false
                                        offsetX.snapTo(0f)
                                    }

                                } else {
                                    resetSlider()
                                }
                            }
                        }
                    ) { _, dragAmount ->

                        scope.launch {
                            offsetX.snapTo(
                                (offsetX.value + dragAmount)
                                    .coerceIn(0f, maxOffset)
                            )
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            CompositionLocalProvider(
                LocalContentColor provides actualContentColor
            ) {
                AnimatedContent(
                    targetState = loading,
                    label = "loading_animation"
                ) { isLoading ->
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = loadingSpinnerColor
                        )
                    } else {
                        icon()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SlideToActionButtonPreview() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF5B5BF7)
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        SlideToActionButton(
            text = "Swipe to download",
            icon = {
                Icon(
                    imageVector = FeatherIcons.Download,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        ) {}

        SlideToActionButton(
            text = "Swipe to pay",
            icon = {
                Text(
                    "$",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        ) {}

        SlideToActionButton(
            text = "Swipe to submit",
            enabled = false,
            icon = {
                Icon(
                    imageVector = FeatherIcons.ArrowDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        ) {}
    }
}