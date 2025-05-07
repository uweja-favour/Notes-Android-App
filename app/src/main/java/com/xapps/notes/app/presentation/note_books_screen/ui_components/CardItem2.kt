package com.xapps.notes.app.presentation.note_books_screen.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xapps.notes.app.presentation.util.Constants.GO_ON
import com.xapps.notes.ui.theme.Dimens


@Composable
fun CardItem2(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    value: Int,
    checkBoxActiveState: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.heightButton),
        shape = RoundedCornerShape(Dimens.radiusMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = !checkBoxActiveState) { onClick() }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = if (checkBoxActiveState) Color.LightGray else Color.Unspecified
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSmall))
                Text(
                    text = text,
                    color = if (checkBoxActiveState) Color.LightGray else Color.Unspecified,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$value",
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
                if (!checkBoxActiveState) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = GO_ON,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
