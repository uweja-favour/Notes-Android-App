package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xapps.notes.app.Logger
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.domain.model.notes_screen.NotesScreenRepo
import com.xapps.notes.app.domain.state.ALL_NOTEBOOK_ID
import com.xapps.notes.app.domain.state.DEFAULT_NOTEBOOK_ID
import com.xapps.notes.app.domain.state.NotesScreenState
import com.xapps.notes.app.domain.state.NotesScreenStateStore
import com.xapps.notes.app.domain.state.RECENTLY_DELETED_NOTEBOOK_ID
import com.xapps.notes.app.domain.state.allNoteBook
import com.xapps.notes.app.domain.state.defaultNoteBook
import com.xapps.notes.app.domain.state.generateUniqueId
import com.xapps.notes.app.domain.state.recentlyDeletedNoteBook
import com.xapps.notes.app.presentation.util.Constants.RECENTLY_DELETED_NOTEBOOK_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SharedViewModel(
    private val repo: NotesScreenRepo
) : ViewModel() {

    val state: StateFlow<NotesScreenState> = NotesScreenStateStore.state

    // Use distinctUntilChanged to avoid unnecessary recompositions
    val totalNoOfUnlockedAndAvailableNotes: Flow<Int> = state
        .map { it.notes.count { note -> note.noteId != "1" && !note.isLocked } }
        .distinctUntilChanged()

    init {
        observeNotesUpdates()
        observeNoteBooksUpdates()
        updateState { it.copy(
            isLoading = false
        ) }
    }

    private fun observeNotesUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeNotes()
                .collectLatest { notes ->
                    updateState { it.copy(
                        notes = notes
                    ) }
                }
        }
    }

    private fun observeNoteBooksUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeNoteBooks()
                .collectLatest { noteBooks ->
                    val orderedNoteBooks = buildList {
                        add(allNoteBook)
                        addAll(noteBooks.filterNot {
                            it.noteBookId in listOf(ALL_NOTEBOOK_ID, DEFAULT_NOTEBOOK_ID, RECENTLY_DELETED_NOTEBOOK_ID) }
                        )
                        add(defaultNoteBook)
                        add(recentlyDeletedNoteBook)
                    }

                    updateStateIfChanged { it.copy(
                        noteBooks = orderedNoteBooks
                    ) }
                }
        }
    }

    suspend fun dispatch(intent: SharedIntent): Boolean {
        return when(intent) {
            is SharedIntent.OnClickNoteBookCard -> {
                updateSelectedNoteBookCard(intent.noteBook)
                true
            }
            is SharedIntent.OnAddNewNoteBook -> {
                addNewNoteBook(intent.noteBookName, intent.noteBookColor)
            }
            is SharedIntent.OnDeleteCheckedNoteBooks -> {
                deleteCheckedNoteBooks(intent.checkedNoteBooksIds)
            }
            is SharedIntent.OnLockCheckedNoteBooks -> {
                lockCheckedNoteBooks(intent.checkedNoteBooksIds)
            }
            is SharedIntent.OnEditCheckedNoteBook -> {
                editCheckedNoteBook(intent.color, intent.title, intent.checkedNoteBookId)
            }
            is SharedIntent.OnToggleNotesScreenEditMode -> {
                onToggleNotesScreenEditMode(intent.editMode)
            }
            is SharedIntent.OnLockCheckedNotes -> {
                onLockCheckedNotes(intent.checkedNotesIds)
            }
            is SharedIntent.OnDeleteCheckedNotes -> {
                onDeleteCheckedNotes(deleteForever = false, intent.checkedNotesIds)
            }
            is SharedIntent.OnDeleteCheckedNotesForever -> {
                onDeleteCheckedNotes(deleteForever = true, intent.checkedNotesIds)
            }
            is SharedIntent.OnUnlockLockedNotes -> {
                onUnLockCheckedNotes(intent.checkedNotesIds)
            }
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

    private suspend fun addNewNoteBook(noteBookTitle: String, noteBookColor: Color): Boolean {
        return try{
            repo.addNoteBook(
                NoteBook(
                    title = noteBookTitle,
                    color = noteBookColor,
                    noteBookId = generateUniqueId()
                )
            )
            true
        } catch (e: Exception) {
            e.message?.let { Logger.logError(it) }
            false
        }
    }

    private suspend fun deleteCheckedNoteBooks(checkedNoteBooksIds: Set<String>): Boolean {
        return try {
            repo.deleteNoteBooks(noteBookIds = checkedNoteBooksIds.toList())
            true
        } catch (e: Exception) {
            e.message?.let { Logger.logError(it) }
            false
        }
    }

    private suspend fun lockCheckedNoteBooks(checkedNoteBooksIds: Set<String>): Boolean {
        return try {
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
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun editCheckedNoteBook(color: Color, title: String, checkedNoteBooksId: String): Boolean {
        return try {
            val state = state.value
            val newNoteBookList = state.noteBooks.fastMap {
                if (it.noteBookId == checkedNoteBooksId) it.copy(color = color, title = title)
                else it
            }
            repo.replaceAllNoteBooks { newNoteBookList }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun onToggleNotesScreenEditMode(notesScreenEditMode: Boolean): Boolean {
        updateState { it.copy(
            notesScreenEditMode = notesScreenEditMode
        ) }
        return true
    }

    private suspend fun onLockCheckedNotes(checkedNotesIds: Set<String>): Boolean {
        return try {
            val currentNotes = state.value.notes

            // Avoid unnecessary allocations if nothing is checked
            if (checkedNotesIds.isEmpty()) return true

            // Efficiently map only when needed
            val updatedNotes = buildList {
                for (note in currentNotes) {
                    if (note.noteId in checkedNotesIds && !note.isLocked) {
                        add(note.copy(isLocked = true)) // use copy to keep other fields
                    } else {
                        add(note)
                    }
                }
            }
            repo.replaceAllNotes{ updatedNotes }
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun onUnLockCheckedNotes(checkedNotesIds: Set<String>): Boolean {
        return try {
            val currentNotes = state.value.notes

            // Avoid unnecessary allocations if nothing is checked
            if (checkedNotesIds.isEmpty()) return true

            // Efficiently map only when needed
            val updatedNotes = buildList {
                for (note in currentNotes) {
                    if (note.noteId in checkedNotesIds && note.isLocked) {
                        add(note.copy(isLocked = false)) // use copy to keep other fields
                    } else {
                        add(note)
                    }
                }
            }
            repo.replaceAllNotes{ updatedNotes }
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun onDeleteCheckedNotes(
        deleteForever: Boolean = false,
        checkedNotesIds: Set<String>
    ): Boolean {
        return try {
            when (deleteForever) {
                true -> {
                    val newNoteList = state.value.notes.filterNot { it.noteId in checkedNotesIds }
                    repo.replaceAllNotes { newNoteList }
                }

                else -> {
                    val newNoteList = state.value.notes.map {
                        if (it.noteId in checkedNotesIds) {
                            Note(
                                heading = it.heading,
                                content = it.content,
                                dateModified = it.dateModified,
                                timeModified = it.timeModified,
                                noteId = it.noteId,
                                noteBookId = RECENTLY_DELETED_NOTEBOOK_ID,
                                noteBookName = RECENTLY_DELETED_NOTEBOOK_NAME,
                                isLocked = it.isLocked
                            )
                        } else
                            it
                    }
                    repo.replaceAllNotes { newNoteList }
                }
            }
            true
        } catch (e: Exception) {
            false
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
