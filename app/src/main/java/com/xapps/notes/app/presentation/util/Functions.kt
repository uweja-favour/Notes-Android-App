package com.xapps.notes.app.presentation.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import com.xapps.notes.AddNoteScreenRoute
import com.xapps.notes.NoteBooksRoute
import com.xapps.notes.NoteViewScreenRoute
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun getCurrentDateTime(): Pair<String, String> {
    val now = LocalDateTime.now()

    val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    val date = now.format(dateFormatter)
    val time = now.format(timeFormatter)

    return Pair(date, time)
}

fun Modifier.onTap(action: () -> Unit): Modifier = pointerInput(Unit) {
    detectTapGestures(onTap = { action() })
}

fun toastThis(msg: String, context: Context, shouldBeLong: Boolean = false) {
    Toast.makeText(context, msg, if (shouldBeLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}


// FOR NAVIGATION...
fun NavHostController.onNavigateToNoteViewScreen(noteId: String) {
    this.navigate(
        NoteViewScreenRoute(
            noteId = Uri.encode(noteId)
        )
    ) {
        launchSingleTop = true
    }
}

fun NavHostController.onNavigateToAddNoteScreen(currentNotebookId: String) {
    this.navigate(
        AddNoteScreenRoute(
            currentNotebookId = Uri.encode(currentNotebookId),
        )
    ) {
        launchSingleTop = true
    }
}

fun NavHostController.onNavigateToNotebookScreen() {
    this.navigate(NoteBooksRoute) { launchSingleTop = true }
}
