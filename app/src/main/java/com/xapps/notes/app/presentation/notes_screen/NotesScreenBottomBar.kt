package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xapps.notes.app.domain.state.RECENTLY_DELETED_NOTEBOOK_ID
import com.xapps.notes.app.presentation.shared_ui_components.BottomBarActionItem
import com.xapps.notes.ui.theme.appContainerColor

@Composable
fun NotesScreenBottomBar(
    currentNoteBookId: String,
    noOfCheckedNotes: Int,
    notesScreenEditMode: Boolean,
    onMoveCheckedNotes: () -> Unit,
    onLockCheckedNotes: () -> Unit,
    onDeleteCheckedNotes: () -> Unit,
    onDeleteCheckedNotesForever: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!notesScreenEditMode) return

    val isEnabled by rememberUpdatedState(noOfCheckedNotes >= 1)
    val (deleteLabel, deleteIcon) = if (currentNoteBookId != RECENTLY_DELETED_NOTEBOOK_ID) {
        "Delete" to Icons.Default.Delete
    } else {
        "Delete Forever" to Icons.Default.DeleteForever
    }

    fun onDeleteClick() {
        if (currentNoteBookId != RECENTLY_DELETED_NOTEBOOK_ID) {
            onDeleteCheckedNotes()
        } else {
            onDeleteCheckedNotesForever()
        }
    }
    NavigationBar(
        modifier = modifier
            .fillMaxHeight(.15f),
        containerColor = appContainerColor,
        contentColor = appContainerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarActionItem(
                icon = Icons.AutoMirrored.Default.DriveFileMove,
                label = "Move",
                enabled = isEnabled,
                onClick = {
                    onMoveCheckedNotes()
                }
            )
            BottomBarActionItem(
                icon = Icons.Default.Lock,
                label = "Lock",
                enabled = isEnabled,
                onClick = {
                    onLockCheckedNotes()
                }
            )
            BottomBarActionItem(
                icon = deleteIcon,
                label = deleteLabel,
                enabled = isEnabled,
                onClick = ::onDeleteClick
            )
        }
    }
}