package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.presentation.note_view_screen.ui_components.OptionsDropdownMenu
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appSurfaceColor
import com.xapps.notes.ui.theme.goldenYellow

@Composable
fun NotesScreenTopBar(
    notesScreenEditMode: Boolean,
    isNotesSortedAlphabetically: Boolean,
    displayedNotes: List<Note>,
    checkedNotesIds: SnapshotStateSet<String>,
    onSearchClick: () -> Unit,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onDeselectAllClick: () -> Unit,
    onSortNotesAlphabetically: (Boolean) -> Unit
) {

    val noOfCheckedNotes by rememberUpdatedState(checkedNotesIds.count())
    val selectAllMode by rememberUpdatedState(checkedNotesIds.size == displayedNotes.size)
    if (notesScreenEditMode) {
        NotesScreenEditModeTopBar(
            noOfCheckedNotes = noOfCheckedNotes,
            selectAllMode = selectAllMode,
            onCancelClick = onCancelClick,
            onSelectAllClick = onSelectAllClick,
            onDeselectAllClick = onDeselectAllClick
        )
    } else {
        DefaultNotesScreenTopBar(
            noOfCheckedNotes = noOfCheckedNotes,
            isNotesSortedAlphabetically = isNotesSortedAlphabetically,
            onSortNotesAlphabetically = onSortNotesAlphabetically,
            onSearchClick = onSearchClick,
            onEditClick = onEditClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesScreenEditModeTopBar(
    noOfCheckedNotes: Int,
    selectAllMode: Boolean,
    onCancelClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onDeselectAllClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(appSurfaceColor),
        navigationIcon = {
           TextButton(
               onClick = {
                    onCancelClick()
               }
           ) {
               Text(
                   text = "Cancel",
                   fontWeight = FontWeight.Bold,
                   fontSize = Dimens.textSubtitle,
                   color = goldenYellow
               )
           }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectAllMode) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "All selected",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle
                    )
                } else {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = " $noOfCheckedNotes selected",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle
                    )
                }
            }
        },
        actions = {
            TextButton(
                onClick = {
                    if (selectAllMode) {
                        onDeselectAllClick()
                    } else {
                        onSelectAllClick()
                    }
                }
            ) {
                if (selectAllMode) {
                    Text(
                        text = "Deselect all",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle,
                        color = goldenYellow
                    )
                } else {
                    Text(
                        text = "Select all",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle,
                        color = goldenYellow
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultNotesScreenTopBar(
    noOfCheckedNotes: Int,
    isNotesSortedAlphabetically: Boolean,
    onSortNotesAlphabetically: (Boolean) -> Unit,
    onSearchClick: () -> Unit,
    onEditClick: () -> Unit
) {
    var optionsDropDownMenuVisibility by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(appSurfaceColor),
        title = {
            Text("")
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier,
                onClick = {
                    onSearchClick()
                }
            )
            IconButton(
                content = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                },
                modifier = Modifier,
                onClick = {
                    optionsDropDownMenuVisibility = !optionsDropDownMenuVisibility
                }
            )
            OptionsDropdownMenu(
                expanded = optionsDropDownMenuVisibility,
                sortNotesAlphabetically = isNotesSortedAlphabetically,
                onEdit = {
                    if (noOfCheckedNotes >= 1) {
                        onEditClick()
                    }
                },
                onNavigateToSettingsScreen = {

                },
                onSortAlphabetically = {
                    onSortNotesAlphabetically(true)
                },
                onSortByTimeEdited = {
                    onSortNotesAlphabetically(false)
                },
                onDismissRequest = {
                    optionsDropDownMenuVisibility = false
                }
            )
        }
    )
}