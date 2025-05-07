package com.xapps.notes.app.presentation.note_books_screen.ui_components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.xapps.notes.app.presentation.util.Constants.GO_ON
import com.xapps.notes.ui.theme.Dimens


@Composable
fun RowWithinACard(
    modifier: Modifier = Modifier,
    text: String,
    value: Int? = null,
    icon: ImageVector,
    checkBoxActiveState: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.heightFiftyDp)
            .padding(horizontal = Dimens.spacingTwelveDp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                tint = if (checkBoxActiveState) {
                    Color.LightGray
                } else Color.Unspecified,
                imageVector = icon,
                contentDescription = text
            )
            Spacer(modifier = Modifier.width(Dimens.spacingSmall))
            Text(
                color = if (checkBoxActiveState) {
                    Color.LightGray
                } else Color.Unspecified,
                text = text,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value != null) { // DEALS WITH LOCKED NOTES
                Text(
                    text = "$value",
                    fontWeight = FontWeight.Light
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = text,
                    modifier = Modifier.size(Dimens.sizeVerySmall)
                )
            }

            if (!checkBoxActiveState) { // ONLY WHEN SELECTION MODE IS FALSE, THAT THE ARROWS WOULD BE PRESENT
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = GO_ON
                )
            }
        }
    }
    HorizontalDivider()
}