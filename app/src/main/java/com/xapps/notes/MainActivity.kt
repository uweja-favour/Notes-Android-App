package com.xapps.notes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xapps.notes.app.domain.state.NotesScreenStateStore
import com.xapps.notes.app.presentation.authentication_screen.AuthenticationScreen
import com.xapps.notes.app.presentation.locked_notes_screen.LockedNotesScreen
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreen
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreenVM
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreen
import com.xapps.notes.app.presentation.notes_screen.NotesScreen
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                NotesApp()
            }
        }
    }

    fun android.content.Context.findActivity(): ComponentActivity {
        var context = this
        while (context is android.content.ContextWrapper) {
            if (context is ComponentActivity) return context
            context = context.baseContext
        }
        throw IllegalStateException("Context is not an Activity")
    }
}

@Composable
private fun NotesApp() {
    val sharedViewModel = koinViewModel<SharedViewModel>()
    val noteBookScreenVM = koinViewModel<NoteBookScreenVM>()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LockedNotesRoute
    ) {
        composable<NotesScreenRoute> {
            NotesScreenRoute(
                sharedViewModel = sharedViewModel,
                navController = navController
            )
        }

        composable<AddNoteRoute> {
            val args = it.toRoute<AddNoteRoute>()
            NoteViewScreen(
                noteBookId = args.noteBookId,
                noteBookName = args.noteBookName,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<ViewNoteRoute> {
            val args = it.toRoute<ViewNoteRoute>()
            NoteViewScreen(
                noteBookId = args.noteBookId,
                noteBookName = args.noteBookName,
                heading = args.heading,
                content = args.content,
                dateModified = args.dateModified,
                timeModified = args.timeModified,
                noteId = args.noteId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NoteBooksRoute> {
            NoteBookScreen(
                sharedViewModel = sharedViewModel,
                noteBookScreenVM = noteBookScreenVM,
                onNavigate = {
                    navController.popBackStack()
                },
                navigateToNoteBook = { noteBookId: String ->
                    NotesScreenStateStore.state.value.noteBooks.firstOrNull { it.noteBookId == noteBookId }?.let { noteBook ->
                        NotesScreenStateStore.update { it.copy(currentNoteBook = noteBook) }
                        navController.popBackStack()
                    } ?: error("Notebook not found. Ensure noteBookId=$noteBookId exists and is not 100.")
                },
                onLockedNotesClick = {
                    navController.navigate(AuthenticationRoute)
                }
            )
        }

        composable<AuthenticationRoute> {
            AuthenticationScreen(
                onAuthSuccess = {
                    navController.navigate(LockedNotesRoute)
                }
            )
        }

        composable<LockedNotesRoute> {
            LockedNotesScreen(
                sharedViewModel = sharedViewModel,
                onBackPress = {
                    navController.navigate(NotesScreenRoute)
                },
                onNavigateToViewExistingNote = { noteBookId, noteBookName, heading, content, dateModified, timeModified, noteId ->
                    navController.navigate(
                        ViewNoteRoute(
                            noteBookId = noteBookId,
                            noteBookName = noteBookName,
                            heading = heading,
                            content = content,
                            dateModified = dateModified,
                            timeModified = timeModified,
                            noteId = noteId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun NotesScreenRoute(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    NotesScreen(
        sharedViewModel = sharedViewModel,
        onNavigateToAddNewNote = { noteBookId, noteBookName ->
            navController.navigate(
                AddNoteRoute(
                    noteBookId = noteBookId,
                    noteBookName = noteBookName
                )
            ) {
                launchSingleTop = true
            }
        },
        onNavigateToViewExistingNote = { noteBookId, noteBookName, heading, content, dateModified, timeModified, noteId ->
            navController.navigate(
                ViewNoteRoute(
                    noteBookId = noteBookId,
                    noteBookName = noteBookName,
                    heading = heading,
                    content = content,
                    dateModified = dateModified,
                    timeModified = timeModified,
                    noteId = noteId
                )
            ) {
                launchSingleTop = true
            }
        },
        onNavigateToNoteBooksScreen = {
            navController.navigate(NoteBooksRoute) {
                launchSingleTop = true
            }
        }
    )
}


@kotlinx.serialization.Serializable
object NotesScreenRoute

@kotlinx.serialization.Serializable
data class AddNoteRoute(
    val noteBookId: String,
    val noteBookName: String
)

@kotlinx.serialization.Serializable
data class ViewNoteRoute(
    val noteBookId: String,
    val noteBookName: String,
    val heading: String,
    val content: String,
    val dateModified: String,
    val timeModified: String,
    val noteId: String
)

@kotlinx.serialization.Serializable
object NoteBooksRoute

@kotlinx.serialization.Serializable
object AuthenticationRoute

@Serializable
object LockedNotesRoute