package com.xapps.notes.app.presentation.notes_screen.ui_components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.data.notes_screen.local.Note

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    checkedNotesIds: SnapshotStateSet<String>,
    checkBoxIsActive: Boolean,
    onCheckedChange: (String, Boolean) -> Unit,
) {
    val title = note.heading.ifBlank { note.content }
    val noteCheckedState by rememberUpdatedState(newValue = (note.noteId in checkedNotesIds))

    Column(modifier = modifier) {
        // Title and Checkbox Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row {
                    if (note.dateModified.isNotBlank()) {
                        Text(
                            text = note.dateModified,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (note.dateModified.isNotBlank() && note.content.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (note.heading.isNotBlank() && note.content.isNotBlank()) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Checkbox(
                checked = noteCheckedState,
                onCheckedChange = {
                    if (checkBoxIsActive) {
                        onCheckedChange(note.noteId, it)
                    }
                },
                modifier = Modifier.alpha(if (checkBoxIsActive) 1f else 0f),
                enabled = checkBoxIsActive
            )
        }
    }
}
