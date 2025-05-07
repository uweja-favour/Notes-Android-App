package com.xapps.notes.app.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

@Composable
fun <T> ObserveAsEvent(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {

    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifeCycleOwner.lifecycle, key1, key2) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

data class AnEvent(
    val eventType: Enum<EventType>,
    val msg: String = ""
)

object EventController {
    private val _event = Channel<AnEvent>()
    val event = _event.receiveAsFlow()

    suspend fun sendEvent(event: AnEvent) {
        _event.send(event)
    }
}

enum class EventType {
    SAVE_NOTE_SUCCESSFULLY,
    SAVE_NOTE_FAILURE
}
