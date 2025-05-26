package com.xapps.notes.app.presentation.notes_screen.ui_components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xapps.notes.R
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.presentation.notes_screen.SharedIntent
import com.xapps.notes.ui.theme.Dimens

@Composable
fun Header(
    modifier: Modifier = Modifier,
    noteBooks: List<NoteBook>,
    totalNumberOfNotes: Int,
    selectedNoteBook: NoteBook,
    navigateToNoteBooksScreen: () -> Unit,
    onClick: (SharedIntent) -> Unit
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
            .padding(
            start = Dimens.spacingMedium,
            end = Dimens.spacingMedium
        )) {
            Text(
                text = stringResource(R.string.notes_screen_title),
                fontWeight = FontWeight.Black,
                fontSize = Dimens.text36Sp
            )
            Text(
                text = stringResource(R.string.notes, totalNumberOfNotes),
                fontWeight = FontWeight.W100,
                fontSize = Dimens.textBody,
                color = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacingSmall))

        Row {
            TinyCard(
                modifier = Modifier
                    .padding(end = 4.dp, top = 8.dp, bottom = 8.dp, start = Dimens.spacingMedium),
                displayNoteBooksScreen = true,
                onNavigateToNoteBooksScreen = navigateToNoteBooksScreen
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .overscroll(rememberOverscrollEffect())
            ) {
                for (noteBook in noteBooks) {
                    TinyCard(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        text = noteBook.noteBookTitle,
                        isSelected = selectedNoteBook.noteBookId == noteBook.noteBookId,
                        noteBook = noteBook,
                        onClick = {
                            onClick(SharedIntent.OnClickNoteBookCard(it))
                        }
                    )
                }
            }
        }
    }
}

