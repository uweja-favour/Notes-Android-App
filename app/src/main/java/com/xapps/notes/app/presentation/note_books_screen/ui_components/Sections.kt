package com.xapps.notes.app.presentation.note_books_screen.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xapps.notes.R
import com.xapps.notes.app.domain.state.NoteBook
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES
import com.xapps.notes.app.presentation.util.Constants.DEFAULT_NOTE_BOOK_ID
import com.xapps.notes.app.presentation.util.Constants.RECENTLY_DELETED_BOOK_ID
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.goldenYellow

@Composable
fun AllNotesSection(
    checkBoxActiveState: Boolean,
    noteCount: Int,
    onClick: () -> Unit
) {
    CardItem2(
        text = ALL_NOTES,
        icon = Icons.AutoMirrored.Filled.EventNote,
        value = noteCount,
        checkBoxActiveState = checkBoxActiveState,
        onClick = onClick
    )
    Spacer(modifier = Modifier.height(Dimens.spacingExtraSmall))
}

@Composable
fun MyNoteBooksHeader(
    checkBoxActiveState: Boolean,
    onAddNewClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.my_notebooks),
            fontWeight = FontWeight.ExtraBold,
            fontSize = Dimens.textBody
        )
        TextButton(
            modifier = Modifier,
            contentPadding = PaddingValues(Dimens.zeroPadding),
            enabled = !checkBoxActiveState,
            onClick = onAddNewClick
        ) {
            Text(
                text = stringResource(R.string.new_),
                color = if (!checkBoxActiveState) goldenYellow else Color.Transparent,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.textBody
            )
        }
    }
}

@Composable
fun MyNoteBooksSection(
    noteBookList: List<NoteBook>,
    checkBoxActiveState: Boolean,
    checkedNoteBookIds: SnapshotStateSet<String>,
    notesPerNotebook: Map<String, Int>,
    onNavigate: (String) -> Unit,
    onCheckChanged: (String, Boolean) -> Unit,
    toggleCheckMode: () -> Unit
) {
    if (noteBookList.size == 1) {
        val noteBook = noteBookList.first()
        CardItem1(
            text = noteBook.title,
            icon = Icons.AutoMirrored.Filled.StickyNote2,
            iconColor = noteBook.color,
            value = notesPerNotebook[noteBook.noteBookId] ?: 0,
            onClick = { onNavigate(noteBook.noteBookId) },
            checkBoxActiveState = checkBoxActiveState,
            onCheckedChange = { onCheckChanged(noteBook.noteBookId, it) },
            toggleCheckboxActiveState = toggleCheckMode,
            isChecked = checkedNoteBookIds.contains(noteBook.noteBookId)
        )
    } else {
        Card(shape = RoundedCornerShape(Dimens.radiusMedium)) {
            noteBookList.forEachIndexed { index, noteBook ->
                CardItem1(
                    text = noteBook.title,
                    icon = Icons.AutoMirrored.Filled.StickyNote2,
                    iconColor = noteBook.color,
                    value = notesPerNotebook[noteBook.noteBookId] ?: 0,
                    onClick = { onNavigate(noteBook.noteBookId) },
                    checkBoxActiveState = checkBoxActiveState,
                    onCheckedChange = { onCheckChanged(noteBook.noteBookId, it) },
                    toggleCheckboxActiveState = toggleCheckMode,
                    isChecked = checkedNoteBookIds.contains(noteBook.noteBookId)
                )
                if (index < noteBookList.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
fun OtherNoteBooksSection(
    checkBoxActiveState: Boolean,
    checkedNoteBookIds: SnapshotStateSet<String>,
    defaultNoteCount: Int,
    deletedNoteCount: Int,
    onNavigate: (String) -> Unit,
    onCheckChanged: (String, Boolean) -> Unit,
    toggleCheckMode: () -> Unit
) {
    val isChecked = checkedNoteBookIds.contains(RECENTLY_DELETED_BOOK_ID)

    Card(shape = RoundedCornerShape(Dimens.radiusMedium)) {
        RowWithinACard(
            modifier = Modifier.clickable(enabled = !checkBoxActiveState) {
                onNavigate(DEFAULT_NOTE_BOOK_ID)
            },
            text = stringResource(R.string.default_notebook),
            value = defaultNoteCount,
            icon = Icons.Default.ContentCopy,
            checkBoxActiveState = checkBoxActiveState
        )
        RowWithinACard(
            modifier = Modifier.clickable(enabled = !checkBoxActiveState) { },
            text = stringResource(R.string.locked_notes),
            icon = Icons.Default.Lock,
            checkBoxActiveState = checkBoxActiveState
        )
        CardItem1(
            text = stringResource(R.string.recently_deleted),
            value = deletedNoteCount,
            icon = Icons.Default.Delete,
            checkBoxActiveState = checkBoxActiveState,
            isChecked = isChecked,
            onClick = { onNavigate(RECENTLY_DELETED_BOOK_ID) },
            onCheckedChange = { onCheckChanged(RECENTLY_DELETED_BOOK_ID, it) },
            toggleCheckboxActiveState = toggleCheckMode
        )
    }
}
