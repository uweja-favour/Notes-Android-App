package com.xapps.notes.app.presentation.locked_notes_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.xapps.notes.app.domain.state.NotesScreenStateStore
import com.xapps.notes.app.presentation.notes_screen.SharedIntent
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.ui.theme.Dimens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LockedNotesScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    onBackPress: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sharedUiState by NotesScreenStateStore.state.collectAsStateWithLifecycle()
    val lockedNotes by produceState(
        initialValue = emptyList(),
        key1 = sharedUiState.notes
    ) {
        value = withContext(Dispatchers.Default) {
            sharedUiState.notes.filter { it.isLocked }
        }
    }
    val checkedNotesIds = remember { mutableStateSetOf<String>() }
    fun handleCheckedChange(noteId: String, selected: Boolean) {
        if (selected) checkedNotesIds.add(noteId) else checkedNotesIds.remove(noteId)
    }
    val isNoteChecked = remember<(String) -> Boolean> {
        { checkedNotesIds.contains(it) }
    }
    val checkBoxIsActive by rememberUpdatedState(
        checkedNotesIds.size >= 1
    )
    val dispatch = remember<suspend (SharedIntent) -> Unit> {
        { sharedViewModel.dispatch(it) }
    }

    BackHandler {
        onBackPress()
    }
    Scaffold(
        topBar = {
            LockedNotesScreenTopBar(
                checkBoxIsActive = checkBoxIsActive,
                onBackPress = { onBackPress() },
                onCancelClick = {
                    scope.launch {
                        checkedNotesIds.clear()
                    }
                },
                onSelectAllClick = {
                    val allIds = lockedNotes.mapTo(HashSet(lockedNotes.size)) { it.noteId }
                    checkedNotesIds.clear()
                    checkedNotesIds.addAll(allIds)
                },
                onDeselectAllClick = {
                    checkedNotesIds.clear()
                },
                noOfCheckedNotes = checkedNotesIds.size,
                lockedNotesSize = lockedNotes.size
            )
        },
        bottomBar = {
            LockedNotesBottomBar(
                noOfCheckedNotes = checkedNotesIds.size,
                checkBoxIsActive = checkBoxIsActive,
                onUnlockClick = {
                    scope.launch {
                        dispatch(
                            SharedIntent.OnUnlockLockedNotes(checkedNotesIds)
                        )
                        checkedNotesIds.clear()
                    }
                },
                onDeleteClick = {
                    scope.launch {
                        dispatch(
                            SharedIntent.OnDeleteCheckedNotes(checkedNotesIds)
                        )
                    }
                    checkedNotesIds.clear()
                },
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .padding(innerPadding)
                .padding(Dimens.spacingMedium)
        ) {
            LockedNotesBody(
                lockedNotes = lockedNotes,
                navController = navController,
                checkBoxIsActive = checkBoxIsActive,
                handleCheckedChange = ::handleCheckedChange,
                isNoteChecked = { noteId -> isNoteChecked(noteId) },
                checkedNotesIds = checkedNotesIds
            )
        }
    }
}