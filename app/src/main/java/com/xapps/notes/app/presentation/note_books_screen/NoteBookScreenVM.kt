package com.xapps.notes.app.presentation.note_books_screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NoteBookScreenState(
    val checkBoxActiveState: Boolean = false
)

class NoteBookScreenVM : ViewModel() {
    private val _state: MutableStateFlow<NoteBookScreenState> = MutableStateFlow(NoteBookScreenState())
    val state: StateFlow<NoteBookScreenState> = _state.asStateFlow()

    fun dispatch(intent: NoteBooksScreenIntent) {
        when(intent) {
            is NoteBooksScreenIntent.OnToggleCheckboxActiveState -> toggleSelectionMode(enabled = intent.enabled)
        }
    }

    private fun toggleSelectionMode(enabled: Boolean) {
        _state.update { it.copy(
            checkBoxActiveState = enabled
        ) }
    }
}


sealed class NoteBooksScreenIntent {
    data class OnToggleCheckboxActiveState(val enabled: Boolean) : NoteBooksScreenIntent()
}