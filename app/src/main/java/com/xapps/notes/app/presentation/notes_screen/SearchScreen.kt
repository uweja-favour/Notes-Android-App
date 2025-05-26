package com.xapps.notes.app.presentation.notes_screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.data.notes_screen.local.Note
import com.xapps.notes.app.presentation.notes_screen.ui_components.PlaceholderView
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appContainerColor
import com.xapps.notes.ui.theme.appSurfaceColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    searchQuery: String,
    noteList: List<Note>,
    onDismiss: () -> Unit,
    updateSearchQuery: (String) -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        shape = RoundedCornerShape(topEnd = Dimens.spacingLarge, topStart = Dimens.spacingLarge),
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = appSurfaceColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchScreenHeader(
                modifier = Modifier.fillMaxWidth(.95f),
                onClick = onDismiss
            )

            Spacer(
                modifier = Modifier.height(Dimens.spacingMedium)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.85f),
                value = searchQuery,
                placeholder = {
                    Text(
                        text = "Search notes..."
                    )
                },
                onValueChange = { updateSearchQuery(it) },
                shape = RoundedCornerShape(Dimens.spacingLarge)
            )

            Spacer(
                modifier = Modifier.height(Dimens.spacingMedium)
            )

            if (noteList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    PlaceholderView(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .wrapContentSize(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.85f)
                        .verticalScroll(rememberScrollState())
                        .overscroll(rememberOverscrollEffect())
                        .background(Color.Red),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        colors = CardDefaults.cardColors(appContainerColor),
                        shape = RoundedCornerShape(Dimens.radiusMedium)
                    ) {
                        noteList.forEachIndexed { index, note ->
                            SearchScreenNoteCard(
                                modifier = Modifier
                                    .padding(Dimens.spacingMedium)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {

                                            }
                                        )
                                    },
                                heading = note.heading,
                                content = note.content,
                                dateModified = note.dateModified
                            )
                            if (index < noteList.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth(.9f)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
private fun SearchScreenNoteCard(
    modifier: Modifier = Modifier,
    dateModified: String,
    content: String,
    heading: String,
) {

    val title = heading.ifBlank { content }

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
                    if (dateModified.isNotBlank()) {
                        Text(
                            text = dateModified,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (dateModified.isNotBlank() && content.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (heading.isNotBlank() && content.isNotBlank()) {
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SearchScreenHeader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "Search",
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.textTitle
        )
    }
}