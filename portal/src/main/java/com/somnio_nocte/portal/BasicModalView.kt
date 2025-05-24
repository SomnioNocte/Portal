package com.somnio_nocte.portal

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
public fun BasicModalView(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
    key: Any? = null,
    surfaceColor: Color = Color.White,
    backgroundColor: Color = Color.Black.copy(.45f),
    shape: Shape = RectangleShape,
    border: BorderStroke? = null,
    opacitySpec: (state: Boolean) -> FiniteAnimationSpec<Float> = { spring(1f, 300f) },
    offsetSpec: (state: Boolean) -> FiniteAnimationSpec<Float> = { spring(1f, 400f) },
    content: @Composable BoxScope.(transition: Transition<Boolean>) -> Unit
) {
    Portal(key) { transition ->
        val opacity by transition.animateFloat(
            transitionSpec = { opacitySpec(transition.targetState) },
            targetValueByState = { if(it) 1f else 0f }
        )

        val offset by transition.animateFloat(
            transitionSpec = { offsetSpec(transition.targetState) },
            targetValueByState = { if(it) 0f else 10f }
        )

        Box(Modifier
            .fillMaxSize()
            .drawBehind {
                if(backgroundColor.alpha != 0f) drawRect(lerp(Color.Transparent, backgroundColor, opacity))
            }
            then
                if(transition.targetState)
                    Modifier.pointerInput(Unit) { detectTapGestures(onTap = { onCloseRequest() }) }
                else
                    Modifier,
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .graphicsLayer {
                    // Float threshold clip fix )))
                    translationY = offset * EaseOutCubic.transform(.1f * size.height)
                }
                .then(
                    if(border != null) Modifier.border(border)
                    else Modifier
                )
                .drawBehind { if(surfaceColor.alpha != 0f) drawRect(surfaceColor) }
                .graphicsLayer {
                    if(shape != RectangleShape) {
                        this.shape = shape
                        clip = true
                    }
                }
                .pointerInput(Unit) { detectTapGestures() }
            ) {
                content(transition)
            }
        }
    }
}