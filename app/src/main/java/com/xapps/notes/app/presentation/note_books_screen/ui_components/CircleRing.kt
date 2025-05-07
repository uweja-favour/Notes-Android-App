package com.xapps.notes.app.presentation.note_books_screen.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.xapps.notes.ui.theme.Dimens

@Composable
fun CircleRing(
    modifier: Modifier = Modifier,
    color: Color = Color.Blue,
    borderWidth: Int = 4,
    isSelected: Boolean
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Get the smallest of the width and height constraints
        val size = minOf(maxWidth, maxHeight)

        Box(
            modifier = Modifier
                .size(size)
                .border(
                    width = borderWidth.dp,
                    color = if (isSelected) color else Color.Transparent,
                    shape = CircleShape
                )
                .padding(Dimens.spacingSmall), // Optional padding for inner spacing
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, CircleShape)
            )
        }
    }
}

