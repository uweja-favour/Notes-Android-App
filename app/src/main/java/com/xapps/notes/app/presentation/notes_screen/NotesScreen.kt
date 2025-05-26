package com.xapps.notes.app.presentation.notes_screen

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xapps.notes.ui.theme.Dimens
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.xapps.notes.app.presentation.notes_screen.ui_components.NotesScreenBody
import com.xapps.notes.app.presentation.notes_screen.ui_components.Header
import com.xapps.notes.app.presentation.shared_ui_components.LoadingScreen
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES
import com.xapps.notes.app.presentation.util.Constants.DEFAULT_NOTE_BOOK
import com.xapps.notes.ui.theme.appSurfaceColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    onNavigateToAddNewNote: (String, String) -> Unit,
    onNavigateToViewExistingNote: (String, String, String, String, String, String, String) -> Unit,
    onNavigateToNoteBooksScreen: () -> Unit
) {
    val uiState by sharedViewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val totalNumberOfNotes by sharedViewModel.totalNoOfUnlockedAndAvailableNotes.collectAsStateWithLifecycle(initialValue = 0)
    val loadingState by rememberUpdatedState(uiState.isLoading)
    val showLoading = rememberSaveable { mutableStateOf(true) }
    var isNotesSortedAlphabetically by remember { mutableStateOf(false) }

//    LaunchedEffect(loadingState) {
//        delay(330L)
//        showLoading.value = loadingState
//    }

    val dispatch = remember<suspend (SharedIntent) -> Unit> { {
        sharedViewModel.dispatch(it)
    } }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("M/d/yyyy", Locale.getDefault()) }
    val displayedNotes by produceState(
        initialValue = emptyList(),
        key1 = isNotesSortedAlphabetically,
        key2 = uiState.currentNoteBook.noteBookId,
        key3 = uiState.notes
    ) {
        value = withContext(Dispatchers.Default) {
            val filteredNotes = uiState.notes
                .asSequence()
                .filterNot { it.noteBookId == "1" || it.isLocked }

            val finalNotes = if (uiState.currentNoteBook.noteBookId == "100") {
                filteredNotes
            } else {
                filteredNotes.filter { it.noteBookId == uiState.currentNoteBook.noteBookId }
            }

            finalNotes.sortedWith(
                if (isNotesSortedAlphabetically)
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it.heading.ifBlank { it.content } }
                else
                    compareBy {
                        try {
                            LocalDate.parse(it.dateModified, dateFormatter)
                        } catch (e: Exception) {
                            LocalDate.MIN // fallback for bad formats
                        }
                    }
            ).toList()
        }
    }

    val checkedNotesIds = remember { mutableStateSetOf<String>() }
    fun handleCheckedChange(noteId: String, selected: Boolean) {
        if (selected) checkedNotesIds.add(noteId) else checkedNotesIds.remove(noteId)
    }

    BackHandler(enabled = uiState.notesScreenEditMode) {
        if (uiState.notesScreenEditMode) {
            scope.launch {
                checkedNotesIds.clear()
                dispatch(
                    SharedIntent.OnToggleNotesScreenEditMode(false)
                )
            }
        }
    }

    if (loadingState) {
        LoadingScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )

    } else {
        Scaffold(
            topBar = {
                NotesScreenTopBar(
                    notesScreenEditMode = uiState.notesScreenEditMode,
                    isNotesSortedAlphabetically = isNotesSortedAlphabetically,
                    displayedNotes = displayedNotes,
                    checkedNotesIds = checkedNotesIds,
                    onSearchClick = {

                    },
                    onEditClick = {
                       scope.launch {
                           dispatch(
                               SharedIntent.OnToggleNotesScreenEditMode(true)
                           )
                       }
                    },
                    onCancelClick = {
                        scope.launch {
                            checkedNotesIds.clear()
                            dispatch(
                                SharedIntent.OnToggleNotesScreenEditMode(false)
                            )
                        }
                    },
                    onSelectAllClick = {
                        val allIds = displayedNotes.mapTo(HashSet(displayedNotes.size)) { it.noteId }
                        checkedNotesIds.clear()
                        checkedNotesIds.addAll(allIds)
                    },
                    onDeselectAllClick = {
                        checkedNotesIds.clear()
                    },
                    onSortNotesAlphabetically = { shouldSortNotesAlphabetically ->
                        isNotesSortedAlphabetically = shouldSortNotesAlphabetically
                    }
                )
            },
            bottomBar = {
                NotesScreenBottomBar(
                    noOfCheckedNotes = checkedNotesIds.size,
                    notesScreenEditMode = uiState.notesScreenEditMode,
                    onMoveCheckedNotes = {

                    },
                    onLockCheckedNotes = {
                        scope.launch {
                            dispatch(
                                SharedIntent.OnLockCheckedNotes(checkedNotesIds)
                            )
                            dispatch(
                                SharedIntent.OnToggleNotesScreenEditMode(false)
                            )
                            checkedNotesIds.clear()
                        }
                    },
                    onDeleteCheckedNotes = {
                        scope.launch {
                            dispatch(
                                SharedIntent.OnDeleteCheckedNotes(checkedNotesIds)
                            )
                            dispatch(
                                SharedIntent.OnToggleNotesScreenEditMode(false)
                            )
                            checkedNotesIds.clear()
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(
                            end = Dimens.spacingSmall,
                            bottom = Dimens.spacingSmall
                        ),
                    onClick = {
                        onNavigateToAddNewNote(
                            if (uiState.currentNoteBook.noteBookId == "100") "0" else uiState.currentNoteBook.noteBookId,
                            if (uiState.currentNoteBook.title == ALL_NOTES) DEFAULT_NOTE_BOOK else uiState.currentNoteBook.title
                        )
                    },
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(Dimens.spacingExtraSmall),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new note",
                        modifier = Modifier.size(Dimens.heightButton)
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                color = appSurfaceColor
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding) // respect FAB padding
                ) {
                    Header(
                        modifier = Modifier
                            .padding(bottom = Dimens.spacingSmall),
                        noteBooks = uiState.noteBooks,
                        totalNumberOfNotes = totalNumberOfNotes,
                        selectedNoteBook = uiState.currentNoteBook,
                        onClick = { scope.launch { dispatch(it) } },
                        navigateToNoteBooksScreen = onNavigateToNoteBooksScreen
                    )
                    NotesScreenBody(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = Dimens.spacingMedium,
                                end = Dimens.spacingMedium
                            ),
                        notesScreenEditModeIsActive = uiState.notesScreenEditMode,
                        displayedNotes = displayedNotes,
                        onClickNote = {
                            onNavigateToViewExistingNote(
                                it.noteBookId,
                                it.noteBookName,
                                it.heading,
                                it.content,
                                it.dateModified,
                                it.timeModified,
                                it.noteId
                            )
                        },
                        toggleNotesScreenEditMode = {
                            scope.launch {
                                dispatch(
                                    SharedIntent.OnToggleNotesScreenEditMode(true)
                                )
                            }
                        },
                        checkedNotesIds = checkedNotesIds,
                        handleCheckedChange = ::handleCheckedChange
                    )
                }
            }
        }
    }
}

