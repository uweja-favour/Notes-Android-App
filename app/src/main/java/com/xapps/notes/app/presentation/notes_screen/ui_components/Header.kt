package com.xapps.notes.app.presentation.notes_screen.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xapps.notes.R
import com.xapps.notes.app.domain.state.NoteBook
import com.xapps.notes.app.presentation.notes_screen.NotesScreenIntent
import com.xapps.notes.ui.theme.Dimens


@Composable
fun Header(
    modifier: Modifier = Modifier,
    noteBooks: List<NoteBook>,
    totalNumberOfNotes: Int,
    selectedNoteBook: NoteBook,
    navigateToNoteBooksScreen: () -> Unit,
    onClick: (NotesScreenIntent) -> Unit
) {

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.notes_screen_title),
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.textHeadline
        )
        Spacer(modifier = Modifier.height(Dimens.spacingExtraSmall))
        Text(
            text = stringResource(R.string.notes, totalNumberOfNotes),
            fontWeight = FontWeight.W100
        )

        Row {
            TinyCard(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                displayNoteBooksScreen = true,
                onNavigateToNoteBooksScreen = navigateToNoteBooksScreen
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(
                    items = noteBooks,
                    key = { it.noteBookId }
                ) { noteBook ->
                    TinyCard(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        text = noteBook.title,
                        isSelected = selectedNoteBook.noteBookId == noteBook.noteBookId,
                        noteBook = noteBook,
                        onClick = {
                            onClick(NotesScreenIntent.OnClickNoteBookCard(it))
                        }
                    )
                }
            }
        }
    }
}

