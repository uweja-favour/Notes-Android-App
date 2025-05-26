package com.xapps.notes.app.presentation.locked_notes_screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.presentation.notes_screen.ui_components.NoteCard
import com.xapps.notes.app.presentation.notes_screen.ui_components.PlaceholderView
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appContainerColor

@Composable
fun LockedNotesBody(
    modifier: Modifier = Modifier,
    lockedNotes: List<Note>,
    checkBoxIsActive: Boolean,
    handleCheckedChange: (String, Boolean) -> Unit,
    isNoteChecked: (String) -> Boolean,
    checkedNotesIds: SnapshotStateSet<String>,
    onNavigateToViewExistingNote: (
        String,
        String,
        String,
        String,
        String,
        String,
        String
    ) -> Unit
) {
    if (lockedNotes.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .overscroll(rememberOverscrollEffect())
        ) {
            Card(
                colors = CardDefaults.cardColors(appContainerColor),
                shape = RoundedCornerShape(Dimens.radiusMedium)
            ) {
                lockedNotes.forEach { note ->
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
                                        }
//                                            noteBookId, noteBookName, heading, content, dateModified, timeModified, noteId
                                        else {
                                            onNavigateToViewExistingNote(
                                                note.noteBookId,
                                                note.noteBookName,
                                                note.heading,
                                                note.content,
                                                note.dateModified,
                                                note.timeModified,
                                                note.noteId
                                            ) // navigates to view notes content
                                        }
                                    },
                                    onLongPress = {
                                        if (!isNoteChecked(note.noteId)) {
                                            handleCheckedChange(
                                                note.noteId,
                                                true
                                            )
                                        }
                                    }
                                )
                            },
                        note = note,
                        checkedNotesIds = checkedNotesIds,
                        checkBoxIsActive = checkBoxIsActive,
                        onCheckedChange = { noteId, isChecked ->
                            handleCheckedChange(noteId, isChecked)
                        }
                    )
                }
            }
        }
    } else {
        PlaceholderView(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .padding(16.dp)
        )
    }
}
