package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Users

data class UsersDTO (
    val email: String,
    val name: String,
    val role: String,
    val courses: List<CourseDTO> = emptyList()
) {
    companion object {
        fun of(user: Users): UsersDTO {
            return UsersDTO(
                email = user.email,
                name = user.name,
                role = user.privileges.name,
//                courses = user.userInCourse.map { CourseDTO.of(it.course) }
                courses = user.userInCourse
                    .mapNotNull {
                        val course = it.course
                        val userRole = it.courseRole?.name
                        if (course != null && userRole != null) CourseDTO.of(course, userRole) else null
                }
            )
        }
    }
}