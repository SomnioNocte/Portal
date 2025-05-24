package com.somnio_nocte.portal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
private fun SampleUseOfPortal() {
    Scaffold {
        var showModalView by rememberSaveable { mutableStateOf(false) }

        if (showModalView) BasicModalView(
            onCloseRequest = { showModalView = false }
        ) {
            Surface {
                Text(
                    "Press the outside of the modal view to close it.",
                    Modifier.fillMaxWidth().padding(82.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(Modifier.fillMaxSize()) {
            FilledTonalButton({ showModalView = true }) {
                Text("Show modal view")
            }
        }
    }
}

@Composable
private fun Text(text: String, modifier: Modifier? = null, textAlign: TextAlign? = null) {  }

@Composable
private fun FilledTonalButton(onClick: () -> Unit, content: @Composable () -> Unit) {  }

@Composable
private fun Scaffold(content: @Composable () -> Unit) {  }

@Composable
private fun Surface(content: @Composable () -> Unit) {  }