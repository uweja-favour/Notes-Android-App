package com.xapps.notes.app.presentation.note_books_screen.ui_components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.presentation.notes_screen.SharedIntent
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.app.presentation.util.Constants.ALL_NOTES_BOOK_ID
import com.xapps.notes.ui.theme.Dimens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
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
    navigateToNotesScreen: (String) -> Unit,
    onLockedNotesClick: () -> Unit,
    onBackPress: () -> Unit
) {
    BackHandler(enabled = checkBoxActiveState, onBack = onBackPress)

    val scope = rememberCoroutineScope()
    val dispatch = remember<suspend (SharedIntent) -> Boolean> { {
        withContext(Dispatchers.IO) {
            sharedViewModel.dispatch(it)
        }
    } }
    var showAddNoteBookBottomSheet by remember { mutableStateOf(false) }
    val addNoteBookSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetHeight = (LocalConfiguration.current.screenHeightDp / 2.5f).dp
    val scrollState = rememberScrollState()
    val overscrollEffect = rememberOverscrollEffect()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .overscroll(overscrollEffect)
    ) {
        AllNotesSection(
            checkBoxActiveState = checkBoxActiveState,
            noteCount = noOfAllNotes,
            onClick = { if (!checkBoxActiveState) navigateToNotesScreen(ALL_NOTES_BOOK_ID) }
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSmall))

        MyNoteBooksHeader(
            checkBoxActiveState = checkBoxActiveState,
            onAddNewClick = { showAddNoteBookBottomSheet = true }
        )
        Spacer(modifier = Modifier.height(Dimens.spacingSmall))



        MyNoteBooksSection(
            noteBookList = noteBookList,
            checkBoxActiveState = checkBoxActiveState,
            checkedNoteBookIds = checkedNoteBookIds,
            notesPerNotebook = notesPerNotebook,
            onNavigate = navigateToNotesScreen,
            onCheckChanged = handleCheckedChange,
            toggleCheckMode = toggleCheckboxActiveState
        )



        if (noteBookList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
        }
        OtherNoteBooksSection(
            checkBoxActiveState = checkBoxActiveState,
            checkedNoteBookIds = checkedNoteBookIds,
            defaultNoteCount = noOfDefaultNotes,
            deletedNoteCount = noOfRecentlyDeletedNotes,
            onNavigate = navigateToNotesScreen,
            onCheckChanged = handleCheckedChange,
            toggleCheckMode = toggleCheckboxActiveState,
            onLockedNotesClick = onLockedNotesClick
        )
    }

    if (showAddNoteBookBottomSheet) {
        AddNoteBookBottomSheet(
            sheetHeight = sheetHeight,
            sheetState = addNoteBookSheetState,
            onSave = { name, color -> dispatch(SharedIntent.OnAddNewNoteBook(name, color)) },
            onDismissRequest = {
                scope.launch {
                    addNoteBookSheetState.hide()
                    showAddNoteBookBottomSheet = false
                }
            }
        )
    }
}
