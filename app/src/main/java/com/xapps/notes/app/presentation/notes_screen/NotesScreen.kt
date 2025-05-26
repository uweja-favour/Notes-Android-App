package com.xapps.notes.app.presentation.notes_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.xapps.notes.app.domain.state.ALL_NOTEBOOK_ID
import com.xapps.notes.app.domain.state.RECENTLY_DELETED_NOTEBOOK_ID
import com.xapps.notes.app.presentation.notes_screen.ui_components.NotesScreenBody
import com.xapps.notes.app.presentation.notes_screen.ui_components.Header
import com.xapps.notes.app.presentation.shared_ui_components.LoadingScreen
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES
import com.xapps.notes.app.presentation.util.Constants.DEFAULT_NOTE_BOOK_NAME
import com.xapps.notes.app.presentation.util.onNavigateToNoteViewScreen
import com.xapps.notes.ui.theme.appSurfaceColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    onNavigateToAddNewNote: (String) -> Unit,
    onNavigateToNoteBooksScreen: () -> Unit
) {

    val uiState by sharedViewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val totalNumberOfNotes by sharedViewModel.totalNoOfUnlockedAndAvailableNotes.collectAsStateWithLifecycle(initialValue = 0)
    val loadingState by rememberUpdatedState(uiState.isLoading)
    val showLoading = rememberSaveable { mutableStateOf(true) }
    var isNotesSortedAlphabetically by remember { mutableStateOf(false) }

    LaunchedEffect(loadingState) {
        delay(330L)
        showLoading.value = loadingState
    }

    val dispatch = remember<suspend (SharedIntent) -> Unit> { {
        withContext(Dispatchers.IO) {
            sharedViewModel.dispatch(it)
        }
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
                .filterNot { it.isLocked } // Remove Locked Notes From the Notes Screen


            val finalNotes = if (uiState.currentNoteBook.noteBookId == ALL_NOTEBOOK_ID) {
                filteredNotes.filterNot { it.noteBookId == RECENTLY_DELETED_NOTEBOOK_ID }
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

    var showSearchScreen by rememberSaveable { mutableStateOf(false) }
    var showAboutAppScreen by rememberSaveable { mutableStateOf(false) }
    val searchScreenSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val aboutScreenSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var job: Job? by remember { mutableStateOf(null) }
    val searchNoteList by produceState(
        initialValue = displayedNotes,
        key1 = searchQuery,
        key2 = displayedNotes
    ) {
        job?.cancel()
        job = null
        job = launch(Dispatchers.Default) {
            value = displayedNotes.filter { it.heading.contains(searchQuery, ignoreCase = true)
            it.content.contains(searchQuery, ignoreCase = true) || it.noteBookName.contains(searchQuery, ignoreCase = true)
            }
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

    if (showLoading.value) {
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
                        scope.launch {
                            showSearchScreen = true
                            searchScreenSheetState.expand()
                        }
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
                    },
                    onAboutAppClick = {
                        scope.launch {
                            showAboutAppScreen = true
                            aboutScreenSheetState.expand()
                        }
                    }
                )
            },
            bottomBar = {
                NotesScreenBottomBar(
                    currentNoteBookId = uiState.currentNoteBook.noteBookId,
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
                    },
                    onDeleteCheckedNotesForever = {
                        scope.launch {
                            dispatch(
                                SharedIntent.OnDeleteCheckedNotesForever(checkedNotesIds)
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
                if (uiState.currentNoteBook.noteBookId != RECENTLY_DELETED_NOTEBOOK_ID) {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(
                                end = Dimens.spacingSmall,
                                bottom = Dimens.spacingSmall
                            ),
                        onClick = {
                            onNavigateToAddNewNote(
                                if (uiState.currentNoteBook.noteBookId == "100") "0" else uiState.currentNoteBook.noteBookId,
//                            if (uiState.currentNoteBook.noteBookTitle == ALL_NOTES) DEFAULT_NOTE_BOOK_NAME else uiState.currentNoteBook.noteBookTitle
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
            }
        ) { innerPadding ->
            Surface(
                color = appSurfaceColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxHeight()
                            .padding(innerPadding)
                            .fillMaxWidth(.95f)
                            .align(Alignment.CenterHorizontally),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                onNavigateToNoteViewScreen(note = it, navController = navController)
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

            if (showSearchScreen) {
                SearchScreen(
                    sheetState = searchScreenSheetState,
                    onDismiss = {
                        scope.launch {
                            searchScreenSheetState.hide()
                            showSearchScreen = false
                        }
                    },
                    noteList = searchNoteList,
                    searchQuery = searchQuery,
                    updateSearchQuery = {
                        searchQuery = it
                    }
                )
            }

            if (showAboutAppScreen) {
                AboutAppScreen(
                    sheetState = aboutScreenSheetState,
                    onDismiss = {
                        scope.launch {
                            aboutScreenSheetState.hide()
                            showAboutAppScreen = false
                        }
                    }
                )
            }
        }
    }
}

