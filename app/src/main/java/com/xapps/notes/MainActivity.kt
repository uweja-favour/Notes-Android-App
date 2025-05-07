package com.xapps.notes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xapps.notes.app.presentation.notes_screen.NotesScreen
import com.xapps.notes.app.presentation.to_dos_screen.TodoScreen
import com.xapps.notes.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.toRoute
import com.xapps.notes.app.domain.state.NotesScreenStateStore
import com.xapps.notes.app.presentation.note_books_screen.NoteBookScreen
import kotlinx.serialization.Serializable
import com.xapps.notes.app.presentation.note_view_screen.NoteViewScreen
import com.xapps.notes.app.presentation.notes_screen.SharedViewModel

private const val pageCount: Int = 2
private var initialPageIndex: MutableIntState = mutableIntStateOf(0)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun NotesApp() {
    val sharedViewModel = hiltViewModel<SharedViewModel>()
    val pagerState = rememberPagerState(
        initialPage = initialPageIndex.intValue,
        pageCount = { pageCount },
    )
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PagerRoute
    ) {
        composable<PagerRoute> {
            MainContent(
                sharedViewModel = sharedViewModel,
                pagerState = pagerState,
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
                onBackPress = {
                    navController.popBackStack()
                },
                navigateToNoteBook = { noteBookId: String ->
                    val noteScreenState = NotesScreenStateStore.state
                    val newCurrentNoteBook = noteScreenState.value.noteBooks.find { it.noteBookId == noteBookId }
                    requireNotNull(newCurrentNoteBook) { "newCurrentNoteBook should not be null. FIX: Check if the noteBookId doesn't exist or is 100. noteBookId: $noteBookId"
                    }
                    NotesScreenStateStore.update { it.copy(
                        currentNoteBook = newCurrentNoteBook
                    ) }
                    navController.popBackStack() // navigate
                }
            )
        }
    }
}


@Composable
private fun MainContent(
    sharedViewModel: SharedViewModel,
    pagerState: PagerState,
    navController: NavController
) {
    HorizontalPager(pagerState) { page ->
        when (page) {
            0 -> NotesScreen(
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
            1 -> TodoScreen()
        }
    }
}


@Serializable
object PagerRoute

@Serializable
data class AddNoteRoute(
    val noteBookId: String,
    val noteBookName: String
)

@Serializable
data class ViewNoteRoute(
    val noteBookId: String,
    val noteBookName: String,
    val heading: String,
    val content: String,
    val dateModified: String,
    val timeModified: String,
    val noteId: String
)

@Serializable
object NoteBooksRoute