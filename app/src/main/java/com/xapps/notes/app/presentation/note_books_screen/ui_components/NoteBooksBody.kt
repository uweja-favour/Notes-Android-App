package com.xapps.notes.app.presentation.note_books_screen.ui_components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.domain.state.NoteBook
import com.xapps.notes.app.presentation.notes_screen.NotesScreenIntent
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES_BOOK_ID
import com.xapps.notes.ui.theme.Dimens

@Composable
fun NoteBooksBody(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    checkBoxActiveState: Boolean,
    notesPerNotebook: Map<String, Int>,
    noOfDefaultNotes: Int,
    noOfRecentlyDeletedNotes: Int,
    noteBookList: List<NoteBook>,
    noOfAllNotes: Int,
    checkedNoteBookIds: SnapshotStateSet<String>,
    handleCheckedChange: (String, Boolean) -> Unit,
    toggleCheckboxActiveState: () -> Unit,
    navigateToNotesScreen: (String) -> Unit
) {
    val dispatch = remember { { intent: NotesScreenIntent -> sharedViewModel.dispatch(intent) } }
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetHeight = (LocalConfiguration.current.screenHeightDp / 2.5f).dp

    LazyColumn(modifier = modifier) {
        item {
            AllNotesSection(
                checkBoxActiveState = checkBoxActiveState,
                noteCount = noOfAllNotes,
                onClick = { if (!checkBoxActiveState) navigateToNotesScreen(ALL_NOTES_BOOK_ID) }
            )

            Spacer(modifier = Modifier.height(Dimens.spacingExtraSmall))

            MyNoteBooksHeader(
                checkBoxActiveState = checkBoxActiveState,
                onAddNewClick = { showBottomSheet = true }
            )
        }

        item {
            MyNoteBooksSection(
                noteBookList = noteBookList,
                checkBoxActiveState = checkBoxActiveState,
                checkedNoteBookIds = checkedNoteBookIds,
                notesPerNotebook = notesPerNotebook,
                onNavigate = navigateToNotesScreen,
                onCheckChanged = handleCheckedChange,
                toggleCheckMode = toggleCheckboxActiveState
            )
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.spacingMedium))

            OtherNoteBooksSection(
                checkBoxActiveState = checkBoxActiveState,
                checkedNoteBookIds = checkedNoteBookIds,
                defaultNoteCount = noOfDefaultNotes,
                deletedNoteCount = noOfRecentlyDeletedNotes,
                onNavigate = navigateToNotesScreen,
                onCheckChanged = handleCheckedChange,
                toggleCheckMode = toggleCheckboxActiveState
            )
        }
    }

    QuarterScreenBottomSheet(
        showSheet = showBottomSheet,
        sheetHeight = sheetHeight,
        onSave = { name, color -> dispatch(NotesScreenIntent.OnAddNewNoteBook(name, color)) },
        onDismissRequest = { showBottomSheet = false }
    )
}
