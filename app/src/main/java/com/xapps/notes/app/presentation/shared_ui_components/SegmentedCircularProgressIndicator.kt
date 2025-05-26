package com.xapps.notes.app.presentation.shared_ui_components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun SegmentedCircularProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    segmentCount: Int = 12,
    segmentColor: Color = MaterialTheme.colorScheme.primary,
    segmentLength: Float = 16f,  // increased length
    segmentWidth: Float = 6f     // increased thickness
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress")

    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = segmentCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing)
        ),
        label = "animatedValue"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = this.center
        val radius = size.toPx() / 2f * 0.7f
        val anglePerSegment = 360f / segmentCount

        for (i in 0 until segmentCount) {
            val angle = Math.toRadians((anglePerSegment * i - 90f).toDouble())
            val startX = center.x + radius * cos(angle).toFloat()
            val startY = center.y + radius * sin(angle).toFloat()
            val endX = center.x + (radius + segmentLength) * cos(angle).toFloat()
            val endY = center.y + (radius + segmentLength) * sin(angle).toFloat()

            // Calculate fade factor using distance from animatedValue
            val distance = (i - animatedValue + segmentCount) % segmentCount
            val fadeFactor = 1f - (distance / segmentCount).coerceIn(0f, 1f)
            val easedFade = fadeFactor * fadeFactor  // quadratic ease for smoother glow

            drawLine(
                color = segmentColor.copy(alpha = easedFade),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = segmentWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
