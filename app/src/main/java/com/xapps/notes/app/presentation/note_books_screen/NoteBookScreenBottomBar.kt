package com.xapps.notes.app.presentation.note_books_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xapps.notes.R
import com.xapps.notes.app.presentation.shared_ui_components.BottomBarActionItem
import kotlinx.coroutines.launch

@Composable
fun NoteBookScreenBottomBar(
    modifier: Modifier = Modifier,
    checkBoxActiveState: Boolean,
    checkedNoteBookIds: SnapshotStateSet<String>,
    onEditClick: () -> Unit = {},
    onLockClick: () -> Unit = {},
    onDeleteClick: suspend () -> Unit = {}
) {
    if (!checkBoxActiveState) return

    val scope = rememberCoroutineScope()
    val editBtnEnabled = checkedNoteBookIds.size == 1 && checkedNoteBookIds.none { it == "1" }
    val lockBtnEnabled = checkedNoteBookIds.isNotEmpty()
    val deleteBtnEnabled = checkedNoteBookIds.isNotEmpty() && checkedNoteBookIds.none { it == "1" }

    NavigationBar(modifier = modifier) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarActionItem(
                modifier = Modifier
                    .weight(1f),
                enabled = editBtnEnabled,
                icon = Icons.Default.Create,
                label = stringResource(R.string.edit),
                onClick = onEditClick
            )

            BottomBarActionItem(
                modifier = Modifier
                    .weight(1f),
                enabled = lockBtnEnabled,
                icon = Icons.Default.Lock,
                label = stringResource(R.string.lock),
                onClick = onLockClick
            )

            BottomBarActionItem(
                modifier = Modifier
                    .weight(1f),
                enabled = deleteBtnEnabled,
                icon = Icons.Default.Delete,
                label = stringResource(R.string.delete),
                onClick = { scope.launch { onDeleteClick() } }
            )
        }
    }
}
