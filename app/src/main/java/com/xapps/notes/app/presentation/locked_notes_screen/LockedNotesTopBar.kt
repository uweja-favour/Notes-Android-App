package com.xapps.notes.app.presentation.locked_notes_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.appSurfaceColor
import com.xapps.notes.ui.theme.goldenYellow

@Composable
fun LockedNotesScreenTopBar(
    modifier: Modifier = Modifier,
    checkBoxIsActive: Boolean,
    lockedNotesSize: Int,
    noOfCheckedNotes: Int,
    onCancelClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onDeselectAllClick: () -> Unit,
    onBackPress: () -> Unit
) {
    val selectAllMode by rememberUpdatedState(lockedNotesSize == noOfCheckedNotes)
    if (checkBoxIsActive) {
        LockedNotesScreenEditModeTopBar(
            noOfCheckedNotes = noOfCheckedNotes,
            selectAllMode = selectAllMode,
            onCancelClick = onCancelClick,
            onSelectAllClick = onSelectAllClick,
            onDeselectAllClick = onDeselectAllClick
        )
    } else {
        DefaultLockedNotesScreenTopBar(
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultLockedNotesScreenTopBar(
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Locked Notes",
                fontWeight = FontWeight.Black,
                fontSize = Dimens.textHeadline
            )
        },

        navigationIcon = {
            IconButton(
                onClick = {
                    onBackPress()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LockedNotesScreenEditModeTopBar(
    noOfCheckedNotes: Int,
    selectAllMode: Boolean,
    onCancelClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onDeselectAllClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(appSurfaceColor),
        navigationIcon = {
            TextButton(
                onClick = {
                    onCancelClick()
                }
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Bold,
                    fontSize = Dimens.textSubtitle,
                    color = goldenYellow
                )
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (selectAllMode) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "All selected",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle
                    )
                } else {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = " $noOfCheckedNotes selected",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle
                    )
                }
            }
        },
        actions = {
            TextButton(
                onClick = {
                    if (selectAllMode) {
                        onDeselectAllClick()
                    } else {
                        onSelectAllClick()
                    }
                }
            ) {
                if (selectAllMode) {
                    Text(
                        text = "Deselect all",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle,
                        color = goldenYellow
                    )
                } else {
                    Text(
                        text = "Select all",
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textSubtitle,
                        color = goldenYellow
                    )
                }
            }
        }
    )
}