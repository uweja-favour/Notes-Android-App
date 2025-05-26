package com.xapps.notes.app.presentation.note_view_screen.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.goldenYellow

@Composable
fun OptionsDropdownMenu(
    expanded: Boolean,
    sortNotesAlphabetically: Boolean,
    onDismissRequest: () -> Unit,
    onEdit: () -> Unit,
    onAboutAppClick: () -> Unit,
    onSortAlphabetically: () -> Unit,
    onSortByTimeEdited: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier.padding( horizontal = Dimens.spacingSmall),
        expanded = expanded,
        onDismissRequest = { onDismissRequest() }
    ) {
        // Wrap content with a Surface to apply the rounded shape
        Column {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    onEdit()
                    onDismissRequest()
                }
            )
            DropdownMenuItem(
                text = { Text("About App") },
                onClick = onAboutAppClick
            )
            HorizontalDivider(thickness = 1.dp)
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Sort alphabetically",
                        color = if (sortNotesAlphabetically) {
                            goldenYellow
                        } else Color.Unspecified
                    )
                },
                onClick = {
                    onDismissRequest()
                    onSortAlphabetically()
                },
                trailingIcon = {
                    if (sortNotesAlphabetically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = goldenYellow
                        )
                    }
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Sort by time edited",
                        color = if (!sortNotesAlphabetically) {
                            goldenYellow
                        } else Color.Unspecified
                    )
                },
                onClick = {
                    onDismissRequest()
                    onSortByTimeEdited()
                },
                trailingIcon = {
                    if (!sortNotesAlphabetically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = goldenYellow
                        )
                    }
                }
            )
        }
    }
}

//@Preview
//@Composable
//fun OptionsDropdownMenuPreview() {
//    OptionsDropdownMenu(
//        onEdit = { println("Edit clicked") },
//        onSettings = { println("Settings clicked") },
//        onSortByTimeCreated = { println("Sort by time created") },
//        onSortByTimeEdited = { println("Sort by time edited") }
//    )
//}
