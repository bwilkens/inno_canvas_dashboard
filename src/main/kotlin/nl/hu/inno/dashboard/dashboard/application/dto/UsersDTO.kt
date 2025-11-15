package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Role
import nl.hu.inno.dashboard.dashboard.domain.Users

data class UsersDTO (
    val email: String,
    val name: String,
    val role: Role?,
    val courses: List<CourseDTO> = emptyList()
) {
    companion object {
        fun of(user: Users?): UsersDTO? {
            if (user == null) return null
            return UsersDTO(
                email = user.email,
                name = user.name,
                role = user.role,
                courses = user.courses.map { CourseDTO.of(it) }
            )
        }
    }
}