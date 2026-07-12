package com.aneez.brandshift.core.designsystem.components

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Supported floating or breathing micro-animations types.
 */
enum class AnimationType {
    FLOAT,
    PULSE
}

/**
 * Reusable icon wrapper applying endless float/pulse animations.
 */
@Composable
fun AnimatedIcon(
    iconResId: Int,
    modifier: Modifier = Modifier,
    animationType: AnimationType = AnimationType.FLOAT
) {
    val infiniteTransition = rememberInfiniteTransition(label = "IconInfiniteTransition")

    val animatedModifier = when (animationType) {
        AnimationType.FLOAT -> {
            val offsetVal by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = EaseInOutQuad),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "FloatingOffsetAnimation"
            )
            Modifier.offset(y = offsetVal.dp)
        }
        AnimationType.PULSE -> {
            val scaleVal by infiniteTransition.animateFloat(
                initialValue = 0.94f,
                targetValue = 1.06f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PulseScaleAnimation"
            )
            Modifier.scale(scaleVal)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Animated Launcher Icon Graphic",
            modifier = Modifier
                .fillMaxSize()
                .then(animatedModifier)
        )
    }
}
