package com.xapps.notes.app.presentation.note_books_screen.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Checkbox
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
fun CardItem1(
    modifier: Modifier = Modifier,
    iconColor: Color = Color.Unspecified,
    icon: ImageVector,
    text: String,
    value: Int,
    checkBoxActiveState: Boolean,
    isChecked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    toggleCheckboxActiveState: () -> Unit
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
                .combinedClickable(
                    onClick = {
                        if (checkBoxActiveState) {
                            onCheckedChange(!isChecked)
                        } else {
                            onClick()
                        }
                    },
                    onLongClick = {
                        if (checkBoxActiveState) {
                            onCheckedChange(!isChecked)
                        } else {
                            toggleCheckboxActiveState()
                            onCheckedChange(true)
                        }
                    }
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    tint = iconColor,
                    imageVector = icon,
                    contentDescription = text
                )
                Spacer(
                    modifier = Modifier.width(Dimens.spacingSmall)
                )
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$value",
                    fontWeight = FontWeight.Light, // make this dull in color
                    color = Color.Gray
                )
                if (!checkBoxActiveState) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = GO_ON, // make this dull in color
                        tint = Color.Gray
                    )
                }
                if (checkBoxActiveState) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange
                    )
                }
            }
        }
    }
}