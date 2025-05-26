package com.xapps.notes.app.presentation.shared_ui_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun BottomBarActionItem(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val contentColor = if (enabled) activeColor else inactiveColor
    val labelHasTwoParts = label.split(" ").size == 2

    Column(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (labelHasTwoParts) {
            val parts = label.split(" ")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = parts.first(),
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = parts.last(),
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        } else {
            Text(
                text = label,
                maxLines = 2,
                textAlign = TextAlign.Center,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
