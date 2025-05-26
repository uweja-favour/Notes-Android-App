package com.xapps.notes.app.presentation.note_view_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.domain.state.generateUniqueId
import com.xapps.notes.app.presentation.util.AnEvent
import com.xapps.notes.app.presentation.util.EventController
import com.xapps.notes.app.presentation.util.EventType
import com.xapps.notes.app.presentation.util.getCurrentDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class NoteViewState(
    val noteBookName: String = "",
    val date: String = "",
    val time: String = "",
    val content: String = "",
    val header: String = ""
)

class NoteViewVM(
    private val repo: NotesScreenRepo
) : ViewModel() {

    fun dispatch(intent: NoteViewEvent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: NoteViewEvent) {
        when(intent) {
            is NoteViewEvent.OnSaveNote -> onSaveNote(
                heading = intent.noteHeading,
                content = intent.noteContent,
                currentNoteBookId = intent.currentNoteBookId,
                currentNoteBookName = intent.currentBookName,
                noteId = intent.noteId
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        val noteContent: String,
        val currentNoteBookId: String,
        val currentBookName: String,
        val noteId: String?
    ) : NoteViewEvent()
}