package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xapps.notes.app.Logger
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.domain.state.NoteBook
import com.xapps.notes.app.domain.state.NotesScreenState
import com.xapps.notes.app.domain.state.NotesScreenStateStore
import com.xapps.notes.app.domain.state.allNoteBook
import com.xapps.notes.app.domain.state.defaultNoteBook
import com.xapps.notes.app.domain.state.recentlyDeletedNoteBook
import com.xapps.notes.app.domain.state.utils.generateUniqueId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repo: NotesScreenRepo
) : ViewModel() {

    val state: StateFlow<NotesScreenState> = NotesScreenStateStore.state

    // Use distinctUntilChanged to avoid unnecessary recompositions
    val totalNoOfUnlockedAndAvailableNotes: Flow<Int> = state
        .map { it.notes.count { note -> note.noteId != "1" && !note.isLocked } }
        .distinctUntilChanged()

    init {
        initialize()
        observeUpdates()
    }

    private fun initialize() {
        viewModelScope.launch {
            val (noteBooks, notes) = repo.retrieveNoteBooks() to repo.retrieveNotes()
            Logger.logData("first; noteBooks retrieved: $noteBooks")
            // Ordered notebooks without unnecessary IDs
            val orderedNoteBooks = buildList {
                add(allNoteBook)
                addAll(noteBooks.filterNot { it.noteBookId in listOf("1", "0", "100") })
                add(defaultNoteBook)
                add(recentlyDeletedNoteBook)  // Adding 'recentlyDeleted'
            }

            updateStateIfChanged { current ->
                val newState = current.copy(
                    noteBooks = orderedNoteBooks,
                    notes = notes,
                    isLoading = false
                )
                if (newState != current) newState else null
            }
            Logger.logData("second; noteBooks retrieved: $orderedNoteBooks")
            Logger.logData("third; noteBooks retrieved: ${state.value.noteBooks}")
        }
    }

    fun dispatch(intent: NotesScreenIntent) {
        when(intent) {
            is NotesScreenIntent.OnClickNoteBookCard -> {
                updateSelectedNoteBookCard(intent.noteBook)
            }
            is NotesScreenIntent.OnAddNewNoteBook -> {
                addNewNoteBook(intent.noteBookName, intent.noteBookColor)
            }
            is NotesScreenIntent.OnDeleteCheckedNoteBooks -> {
                deleteCheckedNoteBooks(intent.checkedNoteBooksIds)
            }
            is NotesScreenIntent.OnLockCheckedNoteBooks -> lockCheckedNoteBooks(intent.checkedNoteBooksIds)
            is NotesScreenIntent.OnEditCheckedNoteBook -> editCheckedNoteBook(intent.color, intent.title, intent.checkedNoteBookId)
        }
    }

    private fun updateSelectedNoteBookCard(newSelectedNoteBook: NoteBook) {
        val currentState = state.value

        if (currentState.currentNoteBook == newSelectedNoteBook) return

        // Only update if something has changed
        updateStateIfChanged {
            it.copy(currentNoteBook = newSelectedNoteBook)
        }
    }

    private fun addNewNoteBook(noteBookTitle: String, noteBookColor: Color) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addNoteBook(
                NoteBook(
                    title = noteBookTitle,
                    color = noteBookColor,
                    noteBookId = generateUniqueId()
                )
            )
        }
    }

    private fun deleteCheckedNoteBooks(checkedNoteBooksIds: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteNoteBooks(noteBookIds = checkedNoteBooksIds.toList())
        }
    }

    private fun lockCheckedNoteBooks(checkedNoteBooksIds: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = state.value
            val newNoteList = state.notes.fastMap {
                if (it.noteBookId in checkedNoteBooksIds) it.copy(isLocked = true)
                else it
            }
            // deleteNoteBooks function attempts to delete the noteBooks whose Ids have been passed
            // as well as the notes that have the same noteBookId as the noteBooks whose Ids were passed
            // Ensure to call the replaceAllNotes after the deleteNoteBooks has completed
            repo.deleteNoteBooks(checkedNoteBooksIds.toList())
            repo.replaceAllNotes { newNoteList }
        }
    }

    private fun editCheckedNoteBook(color: Color, title: String, checkedNoteBooksId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = state.value
            val newNoteBookList = state.noteBooks.fastMap {
                if (it.noteBookId == checkedNoteBooksId) it.copy(color = color, title = title)
                else it
            }
            repo.replaceAllNoteBooks { newNoteBookList }
        }
    }

    private fun observeUpdates() {
        viewModelScope.launch {
            repo.observeNotes().collect { notes ->
                Logger.logData("observeUpdates!!!")

                // Only update if the notes have changed significantly
                updateStateIfChanged { current ->
                    val newState = current.copy(notes = notes)
                    if (newState != current) newState else null
                }
            }
        }
        viewModelScope.launch {
            repo.observeNoteBooks().collect { noteBooks ->
                Logger.logData("observeNoteBooks")

                updateStateIfChanged { current ->
                    val newState = current.copy(noteBooks = noteBooks)
                    if (newState != current) newState else null
                }
            }
        }
    }

    companion object {
        // Avoid unnecessary updates to the state if not changed
        fun updateStateIfChanged(transform: (NotesScreenState) -> NotesScreenState?) {
            NotesScreenStateStore.updateIfChanged(transform)
        }

        // Force update to state when necessary
        fun updateState(transform: (NotesScreenState) -> NotesScreenState) {
            NotesScreenStateStore.update(transform)
        }
    }
}
