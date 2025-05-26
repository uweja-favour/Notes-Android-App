package com.xapps.notes.app.presentation.notes_screen.ui_components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appContainerColor

@SuppressLint("MutableCollectionMutableState")
@Composable
fun NotesScreenBody(
    modifier: Modifier = Modifier,
    notesScreenEditModeIsActive: Boolean,
    checkedNotesIds: SnapshotStateSet<String>,
    displayedNotes: List<Note>,
    handleCheckedChange: (String, Boolean) -> Unit,
    toggleNotesScreenEditMode: () -> Unit,
    onClickNote: (Note) -> Unit
) {

    val checkBoxIsActive by rememberUpdatedState(checkedNotesIds.isNotEmpty() || notesScreenEditModeIsActive)
    fun isNoteChecked(noteId: String) = checkedNotesIds.contains(noteId)

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
            Column(
                modifier = Modifier
                    .overscroll(rememberOverscrollEffect()) // adds the drag/stretch effect
                    .verticalScroll(rememberScrollState()), // enables scrolling
            ) {
                Card(
                    colors = CardDefaults.cardColors(appContainerColor),
                    shape = RoundedCornerShape(Dimens.radiusMedium)
                ) {
                    // card item 1
                    displayedNotes.forEachIndexed { index, note ->
                        NoteCard(
                            modifier = Modifier
                                .padding(Dimens.spacingMedium)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if (checkBoxIsActive) {
                                                handleCheckedChange (
                                                    note.noteId,
                                                    !isNoteChecked(note.noteId)
                                                )
                                            } else {
                                                onClickNote(note) // navigates to view notes content
                                            }
                                        },
                                        onLongPress = {
                                            if (!isNoteChecked(note.noteId)) {
                                                toggleNotesScreenEditMode()
                                                handleCheckedChange(
                                                    note.noteId,
                                                    true
                                                )
                                            }
                                        }
                                    )
                                },
                            checkedNotesIds = checkedNotesIds,
                            checkBoxIsActive = checkBoxIsActive,
                            note = note,
                            onCheckedChange = { noteId, isChecked -> handleCheckedChange(noteId, isChecked) }
                        )
                        if (index < displayedNotes.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(.9f)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(Dimens.spacingLarge)
                        .background(Color.Red)
                )
            }
        }
    }
}

