package com.xapps.notes.app.presentation.note_books_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.xapps.notes.R
import com.xapps.notes.app.presentation.util.Constants.DONE
import com.xapps.notes.app.presentation.util.Constants.SELECT_ITEMS
import com.xapps.notes.app.presentation.util.onTap
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.goldenYellow

@Composable
fun NoteBookScreenTopBar(
    checkBoxActiveState: Boolean = false,
    onBackPress: () -> Unit,
    toggleCheckboxActiveState: (Boolean) -> Unit
) {

    if (checkBoxActiveState) {
        CheckBoxActiveTopBar(toggleCheckboxActiveState)
    } else {
        DefaultTopBar(onBackPress, toggleCheckboxActiveState)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckBoxActiveTopBar(
    onToggleCheckBoxActiveState: (Boolean) -> Unit
) {
    TopAppBar(
//        navigationIcon = {
//            Row {
//                Spacer(
//                    modifier = Modifier
//                        .padding(start = Dimens.spacingSmall)
//                )
//                Text(
//                    modifier = Modifier
//                        .onTap { onToggleCheckBoxActiveState(false) },
//                    text = DONE,
//                    color = goldenYellow,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        },

        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = Dimens.spacingMedium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .onTap { onToggleCheckBoxActiveState(false) },
                        text = DONE,
                        color = goldenYellow,
                        maxLines = 1,
                        fontSize = Dimens.textSubtitle,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = SELECT_ITEMS,
                    maxLines = 1,
                    fontSize = Dimens.textTitle,
                    fontWeight = FontWeight.Black
                )
            }

        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(
    onBackPress: () -> Unit,
    onToggleCheckBoxActiveState: (Boolean) -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .padding(end = Dimens.spacingMedium),
        title = {
            Row {
                Text(
                    text = stringResource(R.string.notebooks),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        navigationIcon = {
            Surface (
                modifier = Modifier
                    .wrapContentSize(),
                shape = CircleShape,
                color = Color.Transparent,
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        },
        actions = {
            Icon(
                modifier = Modifier
                    .onTap { onToggleCheckBoxActiveState(true) },
                imageVector = Icons.Default.Checklist,
                contentDescription = stringResource(R.string.select_items)
            )
        }
    )
}

