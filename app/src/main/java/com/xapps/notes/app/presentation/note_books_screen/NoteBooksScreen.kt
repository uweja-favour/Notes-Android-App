package com.xapps.notes.app.presentation.note_books_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xapps.notes.app.domain.state.Note
import com.xapps.notes.app.domain.state.NoteBook
import com.xapps.notes.app.presentation.note_books_screen.ui_components.NoteBooksBody
import com.xapps.notes.app.presentation.note_books_screen.ui_components.QuarterScreenBottomSheet
import com.xapps.notes.app.presentation.notes_screen.NotesScreenIntent
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.app.presentation.shared_ui_components.LoadingScreen
import com.xapps.notes.ui.theme.Dimens
import kotlinx.coroutines.delay

@Composable
fun NoteBookScreen(
    onBackPress: () -> Unit,
    sharedViewModel: SharedViewModel,
    noteBookScreenVM: NoteBookScreenVM = viewModel(),
    navigateToNoteBook: (String) -> Unit
) {
    val noteScreenState by sharedViewModel.state.collectAsStateWithLifecycle()
    val uiState by noteBookScreenVM.state.collectAsStateWithLifecycle()

    val dispatch = remember<(NoteBooksScreenIntent) -> Unit> {
        { noteBookScreenVM.dispatch(it) }
    }

    val sharedViewModelDispatch = remember<(NotesScreenIntent) -> Unit>{
        { sharedViewModel.dispatch(it) }
    }

    val checkedNoteBookIds = remember { mutableStateSetOf<String>() }
    val checkBoxActiveState = uiState.checkBoxActiveState

    val noteBookList = remember(noteScreenState.noteBooks) {
        noteScreenState.noteBooks.filterNot {
            it.noteBookId in setOf("0", "1", "100")
        }
    }

    val notes = noteScreenState.notes
    val filteredNoteList = remember(notes) {
        notes.filterNot { it.noteBookId == "1" || it.isLocked }
    }

    val noOfDefaultNotes = remember(notes) {
        notes.count { it.noteBookId == "0" }
    }

    val noOfRecentlyDeletedNotes = remember(notes) {
        notes.count { it.noteBookId == "1" }
    }

    val notesPerNotebook = remember(notes) {
        require(notes.none { it.noteBookId == "100" }) {
            "Notebook ID '100' is reserved for All Notes and should not be assigned to any single note."
        }
        notes.groupingBy { it.noteBookId }.eachCount()
    }

    val toggleCheckBoxActiveState = remember<(Boolean) -> Unit>{
        { dispatch(NoteBooksScreenIntent.OnToggleCheckboxActiveState(it)) }
    }

    fun handleCheckedChange(noteBookId: String, isChecked: Boolean) {
        if (isChecked) checkedNoteBookIds.add(noteBookId)
        else checkedNoteBookIds.remove(noteBookId)
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    var editableNotebook by remember { mutableStateOf<NoteBook?>(value = null) }

    fun onEditClickImpl() {
        editableNotebook = noteBookList[0]
        requireNotNull(editableNotebook) { "editableNoteBook should not be null when saving" }
        showBottomSheet = true
    }
    fun onLockClickImpl() {
        sharedViewModelDispatch(
            NotesScreenIntent.OnLockCheckedNoteBooks(
                checkedNoteBookIds
            )
        )
        toggleCheckBoxActiveState(false)
    }
    fun onDeleteClickImpl() {
        sharedViewModelDispatch(
            NotesScreenIntent.OnDeleteCheckedNoteBooks(
                checkedNoteBookIds
            )
        )
        checkedNoteBookIds.clear()
        toggleCheckBoxActiveState(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NoteBookScreenTopBar(
                checkBoxActiveState = checkBoxActiveState,
                onBackPress = onBackPress,
                toggleCheckboxActiveState = {
                    dispatch(NoteBooksScreenIntent.OnToggleCheckboxActiveState(it))
                }
            )
        },
        bottomBar = {
            NoteBookScreenBottomBar(
                checkBoxActiveState = checkBoxActiveState,
                checkedNoteBookIds = checkedNoteBookIds,
                onEditClick = ::onEditClickImpl,
                onLockClick = ::onLockClickImpl,
                onDeleteClick = ::onDeleteClickImpl
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NoteBooksBody(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.spacingMedium),
                sharedViewModel = sharedViewModel,
                notesPerNotebook = notesPerNotebook,
                noOfDefaultNotes = noOfDefaultNotes,
                noOfRecentlyDeletedNotes = noOfRecentlyDeletedNotes,
                noteBookList = noteBookList,
                navigateToNotesScreen = navigateToNoteBook,
                checkBoxActiveState = checkBoxActiveState,
                noOfAllNotes = filteredNoteList.size,
                checkedNoteBookIds = checkedNoteBookIds,
                handleCheckedChange = ::handleCheckedChange,
                toggleCheckboxActiveState = {
                    dispatch(NoteBooksScreenIntent.OnToggleCheckboxActiveState(true))
                }
            )

            QuarterScreenBottomSheet(
                showSheet = showBottomSheet,
                title = editableNotebook?.title,
                color = editableNotebook?.color,
                onSave = { newNoteBookTitle: String, newNoteBookColor: Color, ->
                    requireNotNull(editableNotebook) { "editableNoteBook should not be null when saving" }
                    sharedViewModelDispatch(
                        NotesScreenIntent.OnEditCheckedNoteBook(
                            editableNotebook!!.noteBookId, newNoteBookTitle, newNoteBookColor
                        )
                    )
                    dispatch(
                        NoteBooksScreenIntent.OnToggleCheckboxActiveState(enabled = false)
                    )
                },
                onDismissRequest = {
                    showBottomSheet = false
                }
            )
        }
    }
}
