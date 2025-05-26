package com.xapps.notes.app.presentation.locked_notes_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xapps.notes.app.presentation.shared_ui_components.BottomBarActionItem

@Composable
fun LockedNotesBottomBar(
    modifier: Modifier = Modifier,
    checkBoxIsActive: Boolean,
    onUnlockClick: () -> Unit,
    onDeleteClick: () -> Unit,
    noOfCheckedNotes: Int
) {
    if (!checkBoxIsActive) return
    val isEnabled by rememberUpdatedState(noOfCheckedNotes >= 1)
    NavigationBar(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarActionItem(
                icon = Icons.Default.Cached,
                label = "Unlock",
                enabled = isEnabled,
                onClick = {
                    onUnlockClick()
                }
            )

            BottomBarActionItem(
                icon = Icons.Default.Delete,
                label = "Delete",
                enabled = isEnabled,
                onClick = {
                    onDeleteClick()
                }
            )
        }
    }
}