package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Users

data class StaffDTO (
val email: String,
val name: String,
val appRole: String,
) {
    companion object {
        fun of(user: Users): StaffDTO {
            return StaffDTO(
                email = user.email,
                name = user.name,
                appRole = user.privilege.name,
            )
        }
    }
}