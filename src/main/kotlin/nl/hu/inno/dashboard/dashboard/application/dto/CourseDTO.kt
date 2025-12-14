package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Course
import java.time.LocalDate

data class CourseDTO (
    val canvasCourseId: Int,
    val courseName: String,
    val courseCode: String,
    val instanceName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val roleInCourse: String,
) {
    companion object {
        fun of(course: Course, roleInCourse: String): CourseDTO = CourseDTO(
            course.canvasCourseId,
            course.courseName,
            course.courseCode,
            course.instanceName,
            course.startDate,
            course.endDate,
            roleInCourse
        )
    }
}