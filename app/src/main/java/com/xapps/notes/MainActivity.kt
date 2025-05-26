package com.xapps.notes

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xapps.notes.app.domain.state.NotesScreenStateStore
import com.xapps.notes.app.presentation.add_note_screen.AddNoteScreen
import com.xapps.notes.app.presentation.add_note_screen.AddNoteScreenVM
import com.xapps.notes.app.presentation.authentication_screen.AuthenticationScreen
import com.xapps.notes.app.presentation.locked_notes_screen.LockedNotesScreen
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreen
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreenVM
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreen
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreenVM
import com.xapps.notes.app.presentation.notes_screen.NotesScreen
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel
import com.xapps.notes.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


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
            val addNoteScreenVM = getViewModel<AddNoteScreenVM>(parameters = { parametersOf(args.currentNoteBookId) })
            AddNoteScreen(
                addNoteScreenVM = addNoteScreenVM,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NoteViewScreenRoute> {
            val args = it.toRoute<NoteViewScreenRoute>()
            val noteViewScreenVM = getViewModel<NoteViewScreenVM>(parameters = { parametersOf(args.noteId) })

            NoteViewScreen(
                noteViewScreenVM = noteViewScreenVM,
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
                navController = navController,
            )
        }
    }
}


@Composable
private fun NotesScreenRoute(
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    NotesScreen(
        sharedViewModel = sharedViewModel,
        navController = navController,
        onNavigateToAddNewNote = { noteBookId ->
            navController.navigate(
                AddNoteRoute(
                    currentNoteBookId = noteBookId,
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


@Serializable
object NotesScreenRoute

@Serializable
data class AddNoteRoute(
    val currentNoteBookId: String
)

@Serializable
data class NoteViewScreenRoute(
//    val noteBookId: String,
//    val noteBookName: String,
//    val heading: String,
//    val content: String,
//    val dateModified: String,
//    val timeModified: String,
    val noteId: String
)

@Serializable
object NoteBooksRoute

@Serializable
object AuthenticationRoute

@Serializable
object LockedNotesRoute