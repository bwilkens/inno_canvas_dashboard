package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Users

data class UsersDTO (
    val email: String,
    val name: String,
    val appRole: String,
    val courses: List<CourseDTO> = emptyList()
) {
    companion object {
        fun of(user: Users): UsersDTO {
            return UsersDTO(
                email = user.email,
                name = user.name,
                appRole = user.appRole.name,
                courses = user.userInCourse
                    .mapNotNull {
                        val course = it.course
                        val roleInCourse = it.courseRole?.name
                        if (course != null && roleInCourse != null) CourseDTO.of(course, roleInCourse) else null
                }
            )
        }
    }
}