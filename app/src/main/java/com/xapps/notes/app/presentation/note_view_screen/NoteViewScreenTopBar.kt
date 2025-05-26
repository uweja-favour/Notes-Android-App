package com.xapps.notes.app.presentation.note_view_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xapps.notes.app.presentation.util.getCurrentDateTime
import com.xapps.notes.ui.theme.Dimens

const val MoreOptions = "More options"
const val Share = "Share"
const val Save = "Save"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteViewScreenTopBar(
    modifier: Modifier = Modifier,
    actionList: List<Action>,
    noteBookName: String,
    noteContent: String,
    date: String = "",
    time: String = "",
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onSaveNote: () -> Unit = {}
) {

    val (currentDate, currentTime) = remember {
        getCurrentDateTime()
    }

    Column(
        modifier = Modifier
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Text(text = "") // Replace with dynamic title if needed
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            },
            actions = {
                actionList.forEach { action ->
                    IconButton(
                        onClick = {
                            when (action.name) {
                                Share -> onShareClick()
                                MoreOptions -> onMoreClick()
                                Save -> onSaveNote()
                            }
                        },
                        enabled = action.isEnabled
                    ) {
                        Icon(action.imageVector, contentDescription = action.name)
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMedium)
        ) {
            Text(
                text = if (date.isBlank() && time.isBlank()) {
                    "$currentDate, $currentTime"
                } else {
                    "$date, $time"
                },
                fontSize = Dimens.textCaption
            )

            Text(
                text = " | ",
                fontSize = Dimens.textCaption
            )

            Text(
                text = "${noteContent.replace(" ", "").length}",
                fontSize = Dimens.textCaption
            )

            Text(
                text = " | ",
                fontSize = Dimens.textCaption
            )

            Text(
                text = noteBookName,
                fontSize = Dimens.textCaption
            )
        }
    }
}

