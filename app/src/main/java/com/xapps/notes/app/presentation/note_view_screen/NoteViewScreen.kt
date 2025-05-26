package com.xapps.notes.app.presentation.note_view_screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xapps.notes.app.Logger.logError
import com.xapps.notes.app.presentation.shared_ui_components.LoadingScreen
import com.xapps.notes.app.presentation.shared_ui_components.NoteViewer
import com.xapps.notes.app.presentation.util.Constants.SAVED
import com.xapps.notes.app.presentation.util.EventController
import com.xapps.notes.app.presentation.util.EventType
import com.xapps.notes.app.presentation.util.ObserveAsEvent

@Composable
fun NoteViewScreen (
    onBack: () -> Unit,
    noteViewScreenVM: NoteViewScreenVM
) {
    val uiState by noteViewScreenVM.state.collectAsStateWithLifecycle()
    val context = LocalContext.current


    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var screenIsLoading by rememberSaveable { mutableStateOf(true) }

    var actionList by remember {
        mutableStateOf(listOf(
            Action(
                name = "Save",
                imageVector = Icons.Default.Check,
                isEnabled = false
            )
        ))
    }

    fun actionRefresh() {
        val hasContent = title.isNotBlank() || body.isNotBlank()

        actionList = buildList {
            if (hasContent) {
                add(
                    Action(
                        name = Save,
                        imageVector = Icons.Default.Check,
                        isEnabled = true
                    )
                )
            }
        }
    }


    LaunchedEffect(uiState.heading, uiState.content) {
        title = uiState.heading
        body = uiState.content
        if (title.isNotBlank() || body.isNotBlank()) {
            screenIsLoading = false
        }
    }


    ObserveAsEvent(
        flow = EventController.event
    ) { event ->
        when (event.eventType) {
            EventType.SAVE_NOTE_SUCCESSFULLY -> {
                actionList = listOf(
                    Action(
                        name = Save,
                        imageVector = Icons.Default.Check,
                        isEnabled = false
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
        noteViewScreenVM.dispatch(it)
    } }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            NoteViewScreenTopBar(
                actionList = actionList,
                date = uiState.dateModified,
                time = uiState.timeModified,
                noteBookName = uiState.noteBookName,
                noteContent = body,
                onBackClick = onBack,
                onSaveNote = {
                    dispatch(NoteViewEvent.OnSaveNote(
                        noteHeading = title,
                        noteContent = body
                    ))
                }
            )
        }
    ) { innerPadding ->

        logError("title: $title, content: $body")
        logError("title: ${uiState.heading}, content: ${uiState.content}")
        if (screenIsLoading) {
            LoadingScreen(
                modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
                text = "Loading..."
            )
        } else {
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                NoteViewer(
                    heading = title,
                    content = body,
                    onHeadingChange = { newTitle ->
                        title = newTitle
                        actionRefresh()
                    },
                    onContentChange = { newContent ->
                        body = newContent
                        actionRefresh()
                    },
                    onBack = { onBack() }
                )
            }
        }
    }
}


@Stable
data class Action(
    val name: String,
    val imageVector: ImageVector,
    val isEnabled: Boolean = true
)