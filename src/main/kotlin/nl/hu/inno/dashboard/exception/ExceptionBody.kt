package nl.hu.inno.dashboard.exception

import java.util.Date

data class ExceptionBody(
    val timestamp: Date = Date(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?
)