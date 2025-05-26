package com.xapps.notes.app.presentation.note_view_screen

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
import kotlinx.coroutines.withContext

data class NoteViewState(
    val noteBookName: String = "",
    val date: String = "",
    val time: String = "",
    val content: String = "",
    val header: String = ""
)


data class NoteViewScreenState(
    val heading: String = "",
    val content: String = "",
    val dateModified: String = "",
    val timeModified: String = "",
    val noteId: String = "",
    val noteBookId: String = "",
    val noteBookName: String = "",
    val isLocked: Boolean = false
)

class NoteViewScreenVM(
    private val repo: NotesScreenRepo,
    private val noteId: String
) : ViewModel() {

    private val _state = MutableStateFlow(NoteViewScreenState())
    val state = _state.asStateFlow()

    init {
        getNoteForId()
    }

    private fun getNoteForId() {
        viewModelScope.launch {
            val noteList = repo.retrieveNotes()
            val note = noteList.find { it.noteId == noteId }
            logError("$note")
            if (note != null) {
                _state.update { it.copy(
                    heading = note.heading,
                    content = note.content,
                    dateModified = note.dateModified,
                    timeModified = note.timeModified,
                    noteId = note.noteId,
                    noteBookId = note.noteBookId,
                    noteBookName = note.noteBookName,
                    isLocked = note.isLocked
                ) }
            } else {
                logError("ERROR IN NoteViewScreenVM")
            }
        }
    }

    fun dispatch(intent: NoteViewEvent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: NoteViewEvent) {
        when(intent) {
            is NoteViewEvent.OnSaveNote -> onSaveNote(
                heading = intent.noteHeading,
                content = intent.noteContent,
                currentNoteBookId = state.value.noteBookId,
                currentNoteBookName = state.value.noteBookName,
                noteId = state.value.noteId
            )
        }
    }

    private fun onSaveNote(
        heading: String,
        content: String,
        currentNoteBookId: String,
        currentNoteBookName: String,
        noteId: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateTime = getCurrentDateTime()
            val newNote = Note(
                heading = heading,
                content = content,
                noteBookName = currentNoteBookName,
                noteBookId = currentNoteBookId,
                dateModified = dateTime.first,
                timeModified = dateTime.second,
                noteId = noteId ?: generateUniqueId() // if the note is just been created generate a unique id
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


sealed class NoteViewEvent {
    data class OnSaveNote(
        val noteHeading: String,
        val noteContent: String
    ) : NoteViewEvent()
}