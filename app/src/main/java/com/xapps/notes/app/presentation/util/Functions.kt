package com.xapps.notes.app.presentation.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateTime(): Pair<String, String> {
    val now = LocalDateTime.now()

    val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    val date = now.format(dateFormatter)
    val time = now.format(timeFormatter)

    return Pair(date, time)
}

fun Modifier.onTap(action: () -> Unit): Modifier = pointerInput(Unit) {
    detectTapGestures(onTap = { action() })
}