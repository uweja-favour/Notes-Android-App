package com.xapps.notes.app.presentation.locked_notes_screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.presentation.notes_screen.ui_components.NoteCard
import com.xapps.notes.app.presentation.notes_screen.ui_components.PlaceholderView
import com.xapps.notes.app.presentation.util.onNavigateToNoteViewScreen
import com.xapps.notes.app.presentation.util.toastThis
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appContainerColor

@Composable
fun LockedNotesBody(
    modifier: Modifier = Modifier,
    lockedNotes: List<Note>,
    navController: NavHostController,
    checkBoxIsActive: Boolean,
    handleCheckedChange: (String, Boolean) -> Unit,
    isNoteChecked: (String) -> Boolean,
    checkedNotesIds: SnapshotStateSet<String>
) {

    val context = LocalContext.current
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
                lockedNotes.forEachIndexed { index, note ->
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
                                        else {
                                            try {
                                                navController.onNavigateToNoteViewScreen(note.noteId)
                                            } catch (e: Exception) {
                                                toastThis(msg = "Navigation failed: ${e.message}", context)
                                            }
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
                    if (index < lockedNotes.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
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
