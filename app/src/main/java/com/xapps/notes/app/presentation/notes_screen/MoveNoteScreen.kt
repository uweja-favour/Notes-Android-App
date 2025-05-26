package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.data.notes_screen.local.NoteBook
import com.xapps.notes.app.presentation.notes_screen.ui_components.PlaceholderView
import com.xapps.notes.app.presentation.shared_ui_components.CircleRing
import com.xapps.notes.app.presentation.shared_ui_components.EmptyPlaceholder
import com.xapps.notes.app.presentation.shared_ui_components.SegmentedCircularProgressIndicator
import com.xapps.notes.app.presentation.util.toastThis
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appContainerColor
import com.xapps.notes.ui.theme.appSurfaceColor
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

typealias NotebookId = String
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveNoteScreen(
    sheetState: SheetState,
    noteBookList: List<NoteBook>,
    onMoveNoteToNotebook: suspend(NotebookId) -> Boolean,
    onDismiss: suspend () -> Unit
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isSubmitting by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { scope.launch { onDismiss() } },
        sheetState = sheetState,
        shape = RoundedCornerShape(topEnd = Dimens.spacingLarge, topStart = Dimens.spacingLarge),
        containerColor = appSurfaceColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoveNoteScreenHeader(
                modifier = Modifier.fillMaxWidth(.95f),
                onDismiss = { scope.launch { onDismiss() } }
            )

            Spacer(
                modifier = Modifier.height(Dimens.spacingMedium)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .fillMaxWidth(.85f)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    colors = CardDefaults.cardColors(appContainerColor),
                    shape = RoundedCornerShape(Dimens.radiusMedium)
                ) {
                    noteBookList.forEach { noteBook ->
                        MoveNoteScreenCard(
                            title = noteBook.noteBookTitle,
                            color = noteBook.color,
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            scope.launch {
                                                isSubmitting = true
                                                val success = onMoveNoteToNotebook(noteBook.noteBookId)
                                                if (success) {
                                                    kotlinx.coroutines.delay(1800L)
                                                    toastThis("Moved success", context)
                                                    onDismiss()
                                                }
                                            }
                                        }
                                    )
                                }
                        )
                    }
                }

                if (noteBookList.isEmpty()) {
                    EmptyPlaceholder(
                        text = "No Notebooks",
                        subText = "Add a notebook to get started.",
                        icon = Icons.Default.Book
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isSubmitting) {
                    SegmentedCircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(Dimens.spacingMedium)
            )
        }
    }
}


@Composable
private fun MoveNoteScreenCard(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Title and Checkbox Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            CircleRing(
                modifier = Modifier.size(40.dp),
                isSelected = false,
                color = color
            )
        }
    }
}

@Composable
private fun MoveNoteScreenHeader(
    modifier: Modifier,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart),
            onClick = onDismiss
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "Choose Notebook",
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.textTitle
        )
    }
}