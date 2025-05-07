package com.xapps.notes.app.presentation.notes_screen.ui_components

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.domain.state.Note
import com.xapps.notes.ui.theme.Dimens


@SuppressLint("MutableCollectionMutableState")
@Composable
fun Body(
    modifier: Modifier = Modifier,
    displayedNotes: List<Note>,
    onClickNote: (Note) -> Unit
) {
    val selectedNotesIds = remember { mutableStateSetOf<String>() }
    val checkBoxIsActive by rememberUpdatedState(selectedNotesIds.isNotEmpty())

    fun handleSelectionChange(noteId: String, selected: Boolean) {
        if (selected) selectedNotesIds.add(noteId) else selectedNotesIds.remove(noteId)
    }

    fun noteIsSelected(noteId: String): Boolean = selectedNotesIds.contains(noteId)

    Box(modifier = modifier) {
        if (displayedNotes.isEmpty()) {
            // Show placeholder when no notes are displayed
            PlaceholderView(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp)
            )
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(
                    items = displayedNotes,
                    key = { it.noteId }
                ) { note ->
                    NoteCard(
                        modifier = Modifier
                            .padding(Dimens.spacingSmall)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (checkBoxIsActive) {
                                            handleSelectionChange(
                                                noteId = note.noteId,
                                                selected = !noteIsSelected(note.noteId)
                                            )
                                        } else {
                                            onClickNote(note) // navigates to view notes content
                                        }
                                    },
                                    onLongPress = {
                                        if (!noteIsSelected(noteId = note.noteId)) {
                                            handleSelectionChange(
                                                noteId = note.noteId,
                                                selected = true
                                            )
                                        }
                                    }
                                )
                            },
                        selectedNotesIds = selectedNotesIds,
                        checkBoxIsActive = checkBoxIsActive,
                        note = note,
                        onCheckedChange = ::handleSelectionChange
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacingSmall))
                }
            }
        }
    }
}

