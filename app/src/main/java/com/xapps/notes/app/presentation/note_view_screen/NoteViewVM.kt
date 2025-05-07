package com.xapps.notes.app.presentation.note_view_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xapps.notes.app.Logger
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.domain.state.Note
import com.xapps.notes.app.domain.state.utils.generateUniqueId
import com.xapps.notes.app.presentation.util.AnEvent
import com.xapps.notes.app.presentation.util.EventController
import com.xapps.notes.app.presentation.util.EventType
import com.xapps.notes.app.presentation.util.getCurrentDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteViewState(
    val noteBookName: String = "",
    val date: String = "",
    val time: String = "",
    val content: String = "",
    val header: String = ""
)

@HiltViewModel
class NoteViewVM @Inject constructor(
    private val repo: NotesScreenRepo
) : ViewModel() {

    fun dispatch(intent: NoteViewEvent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: NoteViewEvent) {
        when(intent) {
            is NoteViewEvent.OnSaveNote -> addNewNote(
                heading = intent.noteHeading,
                content = intent.noteContent,
                currentNoteBookId = intent.currentNoteBookId,
                currentNoteBookName = intent.currentBookName,
                noteId = intent.noteId
            )
        }
    }

    private fun addNewNote(
        heading: String,
        content: String,
        currentNoteBookId: String,
        currentNoteBookName: String,
        noteId: String? = null
    ) {
        viewModelScope.launch {
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

            val success = repo.addNote(newNote)
            Logger.logData("addNewNote, success is $success")
            if (success) {
                EventController.sendEvent(
                    AnEvent(
                        eventType = EventType.SAVE_NOTE_SUCCESSFULLY
                    )
                )
            } else {
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
        val noteContent: String,
        val currentNoteBookId: String,
        val currentBookName: String,
        val noteId: String?
    ) : NoteViewEvent()
}