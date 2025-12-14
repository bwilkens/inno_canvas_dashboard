package nl.hu.inno.dashboard.dashboard.presentation.dto

data class UserPutRequest (
    val email: String,
    val name: String,
    val appRole: String,
)