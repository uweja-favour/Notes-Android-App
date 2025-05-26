package com.xapps.notes.app.presentation.note_books_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.presentation.note_books_screen.ui_components.EditBottomSheet
import com.xapps.notes.app.presentation.note_books_screen.ui_components.NoteBooksBody
import com.xapps.notes.app.presentation.notes_screen.SharedIntent
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appSurfaceColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBookScreen(
    sharedViewModel: SharedViewModel,
    noteBookScreenVM: NoteBookScreenVM,
    onNavigate: () -> Unit,
    onLockedNotesClick: () -> Unit,
    navigateToNoteBook: (String) -> Unit
) {

    val scope = rememberCoroutineScope()
    val uiState by noteBookScreenVM.state.collectAsStateWithLifecycle()

    val dispatch = remember<(NoteBooksScreenIntent) -> Boolean> {
        { noteBookScreenVM.dispatch(it) }
    }

    val sharedViewModelDispatch = remember<suspend (SharedIntent) -> Boolean>{
        {
            withContext(Dispatchers.IO) {
                sharedViewModel.dispatch(it)
            }
        }
    }

    val checkedNoteBookIds = remember { mutableStateSetOf<String>() }
    val checkBoxActiveState = uiState.checkBoxActiveState
    val editNoteBookSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showEditNoteBookSheet by remember { mutableStateOf(false) }

    val noteBookList = uiState.noteBookList
    val notes = uiState.noteList

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
    var editableNotebook by remember { mutableStateOf<NoteBook?>(value = null) }

    fun onEditClickImpl() {
        editableNotebook = noteBookList.find { it.noteBookId == checkedNoteBookIds.last() }
        requireNotNull(editableNotebook) { "editableNoteBook should not be null when edit btn was clicked" }
        showEditNoteBookSheet = true
    }
    fun onLockClickImpl() = scope.launch {
        sharedViewModelDispatch(
            SharedIntent.OnLockCheckedNoteBooks(
                checkedNoteBookIds
            )
        )
        toggleCheckBoxActiveState(false)
    }

    fun onDeleteClickImpl() = scope.launch {
       sharedViewModelDispatch(
           SharedIntent.OnDeleteCheckedNoteBooks(
               checkedNoteBookIds
           )
       )
       checkedNoteBookIds.clear()
       toggleCheckBoxActiveState(false)
    }

    fun onBackPress() {
        checkedNoteBookIds.clear()
        toggleCheckBoxActiveState(false)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NoteBookScreenTopBar(
                checkBoxActiveState = checkBoxActiveState,
                onNavigateBack = { onNavigate() },
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
                .padding(innerPadding),
            color = appSurfaceColor
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
                },
                onBackPress = ::onBackPress,
                onLockedNotesClick = { onLockedNotesClick() }
            )

            if (showEditNoteBookSheet) {
                editableNotebook?.color?.let {
                    editableNotebook?.noteBookTitle?.let { it1 ->
                        EditBottomSheet(
                            sheetState = editNoteBookSheetState,
                            title = it1,
                            color = it,
                            onSave = { newNoteBookTitle: String, newNoteBookColor: Color, ->
                                requireNotNull(editableNotebook) { "editableNoteBook should not be null when saving" }
                                val saveComplete = sharedViewModelDispatch(
                                    SharedIntent.OnEditCheckedNoteBook(
                                        editableNotebook!!.noteBookId, newNoteBookTitle, newNoteBookColor
                                    )
                                )
                                if (saveComplete) {
                                    dispatch(
                                        NoteBooksScreenIntent.OnToggleCheckboxActiveState(enabled = false)
                                    )
                                    checkedNoteBookIds.clear()
                                }
                                saveComplete
                            },
                            onDismissRequest = {
                                scope.launch {
                                    editNoteBookSheetState.hide()
                                    showEditNoteBookSheet = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
