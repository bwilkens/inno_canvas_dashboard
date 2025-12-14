package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Users

data class AdminDTO (
val email: String,
val name: String,
val appRole: String,
) {
    companion object {
        fun of(user: Users): AdminDTO {
            return AdminDTO(
                email = user.email,
                name = user.name,
                appRole = user.appRole.name,
            )
        }
    }
}