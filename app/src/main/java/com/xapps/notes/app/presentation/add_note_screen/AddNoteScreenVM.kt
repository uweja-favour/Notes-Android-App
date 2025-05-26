package com.xapps.notes.app.presentation.add_note_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xapps.notes.app.Logger.logError
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.domain.state.generateUniqueId
import com.xapps.notes.app.presentation.util.AnEvent
import com.xapps.notes.app.presentation.util.EventController
import com.xapps.notes.app.presentation.util.EventType
import com.xapps.notes.app.presentation.util.getCurrentDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddNoteScreenState(
    val currentNotebookName: String = ""
)

class AddNoteScreenVM (
    private val repo: NotesScreenRepo,
    private val currentNoteBookId: String
): ViewModel() {
    private val _state = MutableStateFlow(AddNoteScreenState())
    val state = _state.asStateFlow()

    init {
        getNoteBookName()
    }

    fun dispatch(intent: AddNoteScreenIntent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: AddNoteScreenIntent) {
        when(intent) {
            is AddNoteScreenIntent.OnSaveNote -> {
                onSaveNote(
                    heading = intent.heading,
                    content = intent.content,
                    currentNoteBookId = currentNoteBookId
                )
            }
        }
    }

    private fun getNoteBookName() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentNotebookName = repo.retrieveNoteBooks().find { it.noteBookId == currentNoteBookId }?.noteBookTitle ?: onError()
            logError("$currentNotebookName")
            _state.update { it.copy(
                currentNotebookName = currentNotebookName
            ) }
        }
    }

    private fun onError(): String {
        logError("ERROR IN AddNoteScreenVM: Couldn't find noteBookName for currentNoteBookId of -> $currentNoteBookId")
        return "Default"
    }

    private fun onSaveNote(
        heading: String,
        content: String,
        currentNoteBookId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateTime = getCurrentDateTime()
            val newNote = Note(
                heading = heading,
                content = content,
                noteBookName = state.value.currentNotebookName,
                noteBookId = currentNoteBookId,
                dateModified = dateTime.first,
                timeModified = dateTime.second,
                noteId = generateUniqueId()
            )

            try {
                repo.addNote(newNote)
                EventController.sendEvent(
                    AnEvent(
                        eventType = EventType.SAVE_NOTE_SUCCESSFULLY
                    )
                )
            } catch (e: Exception) {
                EventController.sendEvent(
                    AnEvent(
                        eventType = EventType.SAVE_NOTE_FAILURE
                    )
                )
            }
        }
    }
}

sealed class AddNoteScreenIntent {
    data class OnSaveNote(
        val heading: String,
        val content: String
    ) : AddNoteScreenIntent()
}