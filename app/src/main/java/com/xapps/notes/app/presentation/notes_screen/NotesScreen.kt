package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xapps.notes.ui.theme.Dimens
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import com.xapps.notes.app.presentation.notes_screen.ui_components.Body
import com.xapps.notes.app.presentation.notes_screen.ui_components.Header
import com.xapps.notes.app.presentation.shared_ui_components.LoadingScreen
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES
import com.xapps.notes.app.presentation.util.Constants.DEFAULT_NOTE_BOOK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    onNavigateToAddNewNote: (String, String) -> Unit,
    onNavigateToViewExistingNote: (String, String, String, String, String, String, String) -> Unit,
    onNavigateToNoteBooksScreen: () -> Unit
) {
    val uiState by sharedViewModel.state.collectAsStateWithLifecycle()
    val totalNumberOfNotes by sharedViewModel.totalNoOfUnlockedAndAvailableNotes.collectAsState(initial = 0)
    val loadingState by rememberUpdatedState(uiState.isLoading)
    val showLoading = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(loadingState) {
        delay(300)
        showLoading.value = loadingState
    }

    val dispatch = remember<(NotesScreenIntent) -> Unit> { {
        sharedViewModel.dispatch(it)
    } }

    val displayedNotes by produceState(initialValue = emptyList(), uiState.notes, uiState.currentNoteBook.noteBookId) {
        value = withContext(Dispatchers.Default) {
            val filteredNoteList = uiState.notes.filterNot { it.noteBookId == "1" || it.isLocked }
            if (uiState.currentNoteBook.noteBookId == "100") filteredNoteList
            else filteredNoteList.filter { it.noteBookId == uiState.currentNoteBook.noteBookId }
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new note"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding) // respect FAB padding
                    .padding(Dimens.spacingMedium)
            ) {
                Header(
                    modifier = Modifier
                        .padding(bottom = Dimens.spacingSmall),
                    noteBooks = uiState.noteBooks,
                    totalNumberOfNotes = totalNumberOfNotes,
                    selectedNoteBook = uiState.currentNoteBook,
                    onClick = { dispatch(it) },
                    navigateToNoteBooksScreen = onNavigateToNoteBooksScreen
                )
                Body(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.radiusLarge))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .fillMaxSize(),
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
                    }
                )
            }
        }
    }
}

