package com.xapps.notes.app.domain.state.utils

import java.util.UUID

fun generateUniqueId(): String {
    return "${System.currentTimeMillis()}-${UUID.randomUUID()}"
}

