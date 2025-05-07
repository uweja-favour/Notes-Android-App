package com.xapps.notes.app.presentation.note_view_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xapps.notes.app.presentation.util.getCurrentDateTime
import com.xapps.notes.ui.theme.Dimens
import kotlinx.coroutines.flow.StateFlow

const val MoreOptions = "More options"
const val Share = "Share"
const val Save = "Save"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteViewScreenTopBar(
    modifier: Modifier = Modifier,
    actionList: List<Action>,
    noteBookName: String,
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
                val firstAction = actionList.first()
                val secondAction = actionList.last()

                IconButton(
                    onClick = {
                        when (firstAction.name) {
                            Share -> onShareClick()
                            MoreOptions -> onMoreClick()
                            Save -> onSaveNote()
                        }
                    },
                    enabled = firstAction.isEnabled
                ) {
                    Icon(firstAction.imageVector, contentDescription = firstAction.name)
                }
                IconButton(
                    onClick = {
                        when (secondAction.name) {
                            Share -> onShareClick()
                            MoreOptions -> onMoreClick()
                            Save -> onSaveNote()
                        }
                    },
                    enabled = secondAction.isEnabled
                ) {
                    Icon(secondAction.imageVector, contentDescription = secondAction.name)
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
                text = noteBookName,
                fontSize = Dimens.textCaption
            )
        }
    }
}

