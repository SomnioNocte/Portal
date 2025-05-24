# Portal

Declaratively displays content on top of the main UI hierarchy.

Portal is a low-level API for building any components that should be displayed on top of the main interface, for example: modal window, dialog, color picker, date and time picker, etc.

Designed to be minimalistic and unbiased in terms of design (hi material). Simply put a builder with zero style.

In most cases, it is recommended to make a component that conforms to your design guidelines and is originally designed for a specific purpose with specific arguments.

You can also use BasicModalView, BasicDialog if it meets your requirements or use their source code as a starting point to write your own (it's easy).

``` Kotlin
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
```

Before using portals, you need to compose their host (aka Nexus). I recommend calling it at the root of the UI hierarchy, inside the MaterialTheme component (or other similarly meaningful component).

``` Kotlin
setContent {
    AppTheme {
        // Here
        NexusPortal {
            // ...
        }
    }
}
```