package com.xapps.notes.app.presentation.shared_ui_components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun NoteViewer(
    heading: String,
    content: String,
    onHeadingChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit
) {

    BackHandler(enabled = true) {
        onBack()
    }
    var isHeaderFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var contentFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = content,
                selection = TextRange(content.length) // initial cursor at end
            )
        )
    }
    var hasFocusedOnce by remember { mutableStateOf(false) }

    // Keep contentFieldValue in sync if `content` changes externally
    LaunchedEffect(content) {
        if (content != contentFieldValue.text) {
            contentFieldValue = TextFieldValue(
                text = content,
                selection = TextRange(content.length)
            )
        }
    }

    // Request focus when the Composable is first drawn
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            BasicTextField(
                value = heading,
                maxLines = 1,
                onValueChange = { onHeadingChange(it) },
                textStyle = TextStyle(
                    color = if (heading.isEmpty() && !isHeaderFocused)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(16.dp)
                    .onFocusChanged { focusState ->
                        isHeaderFocused = focusState.isFocused
                    }
            ) {
                if (heading.isEmpty() && !isHeaderFocused) {
                    Text(
                        text = "Heading",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                    )
                } else {
                    it()
                }
            }
        }

        item {
            BasicTextField(
                value = contentFieldValue,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                ),
                onValueChange = {
                    contentFieldValue = it
                    onContentChange(it.text)
                },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused && !hasFocusedOnce) {
                            hasFocusedOnce = true
                            contentFieldValue = contentFieldValue.copy(
                                selection = TextRange(contentFieldValue.text.length)
                            )
                        }
                    }
            )
        }
    }
}