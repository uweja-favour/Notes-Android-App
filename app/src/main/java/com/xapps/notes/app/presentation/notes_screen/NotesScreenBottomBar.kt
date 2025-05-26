package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.presentation.shared_ui_components.BottomBarActionItem
import com.xapps.notes.ui.theme.appContainerColor
import com.xapps.notes.ui.theme.appSurfaceColor

@Composable
fun NotesScreenBottomBar(
    noOfCheckedNotes: Int,
    notesScreenEditMode: Boolean,
    onMoveCheckedNotes: () -> Unit,
    onLockCheckedNotes: () -> Unit,
    onDeleteCheckedNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!notesScreenEditMode) return

    val isEnabled by rememberUpdatedState(noOfCheckedNotes >= 1)
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
                icon = Icons.Default.Delete,
                label = "Delete",
                enabled = isEnabled,
                onClick = {
                    onDeleteCheckedNotes()
                }
            )
        }
    }
}