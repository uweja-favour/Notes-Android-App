package com.xapps.notes.app.presentation.note_books_screen.ui_components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xapps.notes.R
import com.xapps.notes.app.Logger
import com.xapps.notes.app.presentation.util.onTap
import com.xapps.notes.ui.theme.Dimens
import com.xapps.notes.ui.theme.aestheticHue
import com.xapps.notes.ui.theme.goldenYellow
import com.xapps.notes.ui.theme.lavender
import com.xapps.notes.ui.theme.peach
import com.xapps.notes.ui.theme.rosePink
import com.xapps.notes.ui.theme.skyBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuarterScreenBottomSheet(
    showSheet: Boolean,
    sheetHeight: Dp = (LocalConfiguration.current.screenHeightDp / 2.5f).dp,
    title: String? = null,
    color: Color? = null,
    onSave: (String, Color) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }


    val colorList = listOf(
        goldenYellow,
        peach,
        rosePink,
        skyBlue,
        lavender,
        aestheticHue
    )
    var noteBookName by remember { mutableStateOf(value = title ?: "") }
    var contentFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = noteBookName,
                selection = TextRange(noteBookName.length)
            )
        )
    }
    var chosenColor by remember { mutableStateOf(value = color ?: colorList[0]) }
    fun setChosenColor(color: Color) {
        chosenColor = color
    }

    LaunchedEffect(title, color) {
        noteBookName = title ?: ""
        if (noteBookName.isNotBlank()) {
            contentFieldValue = TextFieldValue(
                text = noteBookName,
                selection = TextRange(noteBookName.length)
            )
        }
        chosenColor = color ?: colorList[0]
    }

    suspend fun hideSheet() {
        noteBookName = ""
        chosenColor = colorList[0]
        sheetState.hide()
        onDismissRequest()
        Logger.logData("Hiding bottom sheet")
    }

    suspend fun expandSheet() {
        Logger.logData("Expanding bottom sheet")
        sheetState.expand()
    }

    LaunchedEffect(key1 = showSheet) {
        Logger.logData("Expanding bottom sheet 1")
        if (showSheet) {
            expandSheet()
        }
    }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Expanded) {
            Logger.logData("Sheet expanded, requesting focus.")
            focusRequester.requestFocus()
        }
    }


    if (showSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch { hideSheet() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.spacingMedium)
                    .padding(bottom = Dimens.spacingMedium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.spacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                hideSheet()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Bold,
                            fontSize = Dimens.textSubtitle,
                            color = goldenYellow
                        )
                    }
                    Text(
                        text = stringResource(R.string.new_notebook),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = Dimens.textTitle
                    )
                    TextButton(
                        onClick = {
                            scope.launch {
                                onSave(noteBookName, chosenColor)
                                hideSheet()
                                onDismissRequest()
                            }
                        },
                        enabled = noteBookName.isNotBlank()
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            fontWeight = FontWeight.Bold,
                            fontSize = Dimens.textSubtitle,
                            color = if (noteBookName.isNotBlank()) goldenYellow else Color.LightGray
                        )
                    }
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.spacingSmall)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (!it.isFocused) return@onFocusChanged
                            contentFieldValue = contentFieldValue.copy(
                                selection = TextRange(noteBookName.length)
                            )
                        },
                    value = contentFieldValue,
                    onValueChange = {
                        contentFieldValue = it
                        noteBookName = it.text
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Article,
                            contentDescription = null,
                            tint = chosenColor
                        )
                    },
                    maxLines = 1,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.notebook_name),
                            color = Color.LightGray
                        )
                    }
                )

                Card(
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = RoundedCornerShape(Dimens.radiusMedium)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(Dimens.spacingSmall)
                    ) {
                        Text(
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            text = stringResource(R.string.select_color),
                            fontSize = Dimens.textCaption,
                            color = Color.DarkGray
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.spacingSmall)
                                .height(60.dp), // Ensures there's a vertical constraint
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            colorList.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .onTap { setChosenColor(color) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircleRing(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        color = color,
                                        isSelected = chosenColor == color
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp)) // Optional spacing
                            }
                        }
                    }
                }
            }
        }
    }
}