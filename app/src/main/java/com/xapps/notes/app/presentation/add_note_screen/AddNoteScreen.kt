package com.xapps.notes.app.presentation.add_note_screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xapps.notes.app.presentation.note_view_screen.Action
import com.xapps.notes.app.presentation.note_view_screen.MoreOptions
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreenTopBar
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreen
import com.xapps.notes.app.presentation.note_view_screen.Save
import com.xapps.notes.app.presentation.shared_ui_components.NoteViewer
import com.xapps.notes.app.presentation.util.Constants.SAVED
import com.xapps.notes.app.presentation.util.EventController
import com.xapps.notes.app.presentation.util.EventType
import com.xapps.notes.app.presentation.util.ObserveAsEvent
import com.xapps.notes.app.presentation.util.getCurrentDateTime
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddNoteScreen(
    onBack: () -> Unit,
    addNoteScreenVM: AddNoteScreenVM
) {

    var heading by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val uiState by addNoteScreenVM.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
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
        val hasContent = heading.isNotBlank() || content.isNotBlank()

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

    val dispatch = remember<(AddNoteScreenIntent) -> Unit>{ {
        addNoteScreenVM.dispatch(it)
    } }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            val (date, time) = getCurrentDateTime()
            NoteViewScreenTopBar(
                actionList = actionList,
                date = date,
                time = time,
                noteBookName = uiState.currentNotebookName,
                noteContent = content,
                onBackClick = onBack,
                onSaveNote = {
                    dispatch(
                        AddNoteScreenIntent.OnSaveNote(
                            heading = heading,
                            content = content,
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            NoteViewer(
                heading = heading,
                content = content,
                onHeadingChange = { newTitle ->
                    heading = newTitle
                    actionRefresh()
                },
                onContentChange = { newContent ->
                    content = newContent
                    actionRefresh()
                },
                onBack = { onBack() }
            )
        }
    }
}
