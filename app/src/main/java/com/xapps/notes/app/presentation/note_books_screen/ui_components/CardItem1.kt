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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.xapps.notes.ui.theme.appContainerColor

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

@Composable
fun NoteCardItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    value: Int? = null,
    isCheckboxMode: Boolean,
    isChecked: Boolean = false,
    onClick: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    toggleCheckboxActiveState: (() -> Unit)? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
) {
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val contentColor = if (isCheckboxMode && toggleCheckboxActiveState == null) {
        inactiveColor
    } else {
        Color.Unspecified
    }
    val trailingIconColor = inactiveColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.heightButton),
        colors = CardDefaults.cardColors(appContainerColor),
        shape = RoundedCornerShape(Dimens.radiusMedium),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = {
                        if (isCheckboxMode) {
                            onCheckedChange?.invoke(!isChecked)
                        } else {
                            onClick()
                        }
                    },
                    onLongClick = {
                        if (isCheckboxMode) {
                            onCheckedChange?.invoke(!isChecked)
                        } else {
                            toggleCheckboxActiveState?.invoke()
                            onCheckedChange?.invoke(true)
                        }
                    }
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Icon and text
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = if (isCheckboxMode && toggleCheckboxActiveState == null) inactiveColor else iconColor
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSmall))
                Text(
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Right side: Value + checkbox or arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (value == null) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = inactiveColor
                    )
                } else {
                    Text(
                        text = "$value",
                        fontWeight = FontWeight.Light,
                        color = inactiveColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isCheckboxMode && toggleCheckboxActiveState != null) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = GO_ON,
                        tint = trailingIconColor
                    )
                }
            }
        }
    }
}
