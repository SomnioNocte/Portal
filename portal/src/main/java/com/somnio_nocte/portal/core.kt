package com.somnio_nocte.portal

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * A low-level composable component that allows content to be displayed on top of an entire application section (depending on where nexus is placed) from any section of a Composable function in declarative form. It is the foundation for modal views, dialogs and snackbars.
 *
 * @sample SampleUseOfPortal
 *
 * */
@Composable
fun Portal(
    key: Any? = null,
    content: @Composable (transition: Transition<Boolean>) -> Unit
) {
    val key = key ?: currentCompositeKeyHash

    key(key) {
        val nexus = LocalNexusPortal.current
        val portal = nexus.getPortalInstance(key) { transition ->
            content(transition)
        }

        DisposableEffect(Unit) {
            portal.state.targetState = true
            nexus.signPortal(portal)

            onDispose {
                portal.state.targetState = false
            }
        }
    }
}

@Composable
private fun SubComposePortal(
    portal: INexusPortal.IPortal
) {
    Box(Modifier.fillMaxSize()) {
        val transition = rememberTransition(portal.state)

        portal.content(transition)
    }
}

@Composable
fun NexusPortal(
    content: @Composable BoxScope.() -> Unit
) {
    val nexusPortal = remember { INexusPortal() }

    LaunchedEffect(Unit) {
        snapshotFlow { nexusPortal.portals.toList() }.collectLatest { portals ->
            portals.fastForEach { portal ->
                launch {
                    portal.shouldDispose
                        .filter { it }
                        .collectLatest { nexusPortal.portals.remove(portal) }
                }
            }
        }
    }

    CompositionLocalProvider(LocalNexusPortal provides nexusPortal) {
        SubcomposeLayout(Modifier.fillMaxSize()) { constraints ->
            val contentPlaceable = subcompose("content") { Box { content() } }
                .first()
                .measure(constraints)

            val portalPlaceables = nexusPortal.portals.fastMap {
                subcompose(it.key) { SubComposePortal(it) }
                    .first()
                    .measure(constraints)
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                contentPlaceable.place(IntOffset.Zero)

                portalPlaceables.fastForEach {
                    it.place(IntOffset.Zero)
                }
            }
        }
    }
}

val LocalNexusPortal = staticCompositionLocalOf<INexusPortal> { error("Nexus Portal not initialized yet") }

@Immutable
class INexusPortal {
    @Stable
    internal data class IPortal(
        val key: Any,
        val content: @Composable (transition: Transition<Boolean>) -> Unit
    ) {
        val shouldDispose = snapshotFlow { !state.currentState && state.isIdle && !state.targetState }
        internal var state = MutableTransitionState(false)
        override fun toString() = "Portal = [key = ${key}, targetState = ${state.targetState}, currentState = ${state.currentState}]"
    }

    internal val portals = mutableStateListOf<IPortal>()
    internal fun signPortal(portal: IPortal) {
        if(portals.none { it.key == portal.key }) portals.add(portal)
    }

    @Composable
    internal fun getPortalInstance(
        key: Any,
        content: @Composable (transition: Transition<Boolean>) -> Unit
    ): IPortal {
        return remember(key, content) { portals.firstOrNull { it.key == key } ?: IPortal(key, content) }
    }

    override fun toString(): String {
        return "NexusPortal = [stackSize = ${portals.size}, stack = [${portals.fastJoinToString(", ") { it.toString() }}]]"
    }
}