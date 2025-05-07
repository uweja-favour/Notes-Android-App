package com.xapps.notes.app.presentation.note_view_screen

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xapps.notes.app.presentation.util.Constants.SAVED
import com.xapps.notes.app.presentation.util.EventController
import com.xapps.notes.app.presentation.util.EventType
import com.xapps.notes.app.presentation.util.ObserveAsEvent

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteViewScreen(
    noteBookId: String,
    noteBookName: String,
    heading: String = "",
    content: String = "",
    dateModified: String = "",
    timeModified: String = "",
    noteId: String? = null,
    onBack: () -> Unit,
    viewModel: NoteViewVM = hiltViewModel<NoteViewVM>()
) {
    var title by remember { mutableStateOf(heading) }
    var body by remember { mutableStateOf(content) }
    val context = LocalContext.current
    var actionList by remember {
        mutableStateOf(listOf(
            Action(
                name = "Share",
                imageVector = Icons.Default.Share,
                isEnabled = false
            ),
            Action(
                name = "More options",
                imageVector = Icons.Default.MoreVert,
                isEnabled = false
            )
        ))
    }

    ObserveAsEvent(
        flow = EventController.event
    ) { event ->
        when (event.eventType) {
            EventType.SAVE_NOTE_SUCCESSFULLY -> {
                actionList = listOf(
                    Action(
                        name = Share,
                        imageVector = Icons.Default.Share
                    ),
                    Action(
                        name = MoreOptions,
                        imageVector = Icons.Default.MoreVert
                    )
                )
                Toast.makeText(context, SAVED, Toast.LENGTH_SHORT).show()
            }
            EventType.SAVE_NOTE_FAILURE -> {
                Toast.makeText(context, "FAILED", Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    BackHandler {
        onBack()
    }

    val dispatch = remember<(NoteViewEvent) -> Unit>{ {
        viewModel.dispatch(it)
    } }

    fun actionRefresh() {
        val hasContent = title.isNotBlank() || body.isNotBlank()

        actionList = buildList {
            add(
                Action(
                    name = Share,
                    imageVector = Icons.Default.Share,
                    isEnabled = hasContent,
                )
            )

            if (hasContent) {
                add(
                    Action(
                        name = Save,
                        imageVector = Icons.Default.Check
                    )
                )
            } else {
                add(
                    Action(
                        name = MoreOptions,
                        imageVector = Icons.Default.MoreVert,
                        isEnabled = hasContent
                    )
                )
            }
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            NoteViewScreenTopBar(
                actionList = actionList,
                date = dateModified,
                time = timeModified,
                noteBookName = noteBookName,
                onBackClick = onBack,
                onSaveNote = {
                    dispatch(NoteViewEvent.OnSaveNote(
                        noteHeading = title,
                        noteContent = body,
                        currentNoteBookId = noteBookId,
                        currentBookName = noteBookName,
                        noteId = noteId
                    ))
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Body(
                heading = title,
                content = body,
                onHeadingChange = { newTitle ->
                    title = newTitle
                    actionRefresh()
                },
                onContentChange = { newContent ->
                    body = newContent
                    actionRefresh()
                }
            )
        }
    }
}

@Stable
data class Action(
    val name: String,
    val imageVector: ImageVector,
    val isEnabled: Boolean = true
)

@Composable
fun Body(
    heading: String,
    content: String,
    onHeadingChange: (String) -> Unit,
    onContentChange: (String) -> Unit
) {
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
                    color = if (heading.isEmpty() && !isHeaderFocused) Color.Gray else Color.Black,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                ),
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
                            color = Color.Gray,
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
                onValueChange = {
                    contentFieldValue = it
                    onContentChange(it.text)
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
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
