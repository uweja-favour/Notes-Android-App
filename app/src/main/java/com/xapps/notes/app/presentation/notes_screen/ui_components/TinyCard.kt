package com.xapps.notes.app.presentation.notes_screen.ui_components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xapps.notes.R
import com.xapps.notes.app.domain.state.NoteBook


@Composable
fun TinyCard(
    modifier: Modifier = Modifier,
    text: String = "",
    isSelected: Boolean = false,
    noteBook: NoteBook? = null,
    displayNoteBooksScreen: Boolean = false,
    onClick: (NoteBook) -> Unit = {},
    onNavigateToNoteBooksScreen: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "CardColorAnimation"
    )

    val textStyle = if (isSelected) {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    when {
                        displayNoteBooksScreen -> {
                            onNavigateToNoteBooksScreen()
                        }

                        else -> {
                            onClick(noteBook!!)
                        }
                    }
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            when {
                displayNoteBooksScreen -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                        contentDescription = stringResource(R.string.note_book_screen),
                        modifier = Modifier.size(20.dp)
                    )
                }
                else -> {
                    Text(
                        text = text,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
        }
    }
}
