package nl.hu.inno.dashboard.dashboard.application.dto

import nl.hu.inno.dashboard.dashboard.domain.Course
import java.time.LocalDate

data class CourseDTO (
    val canvasCourseId: Int,
    val courseName: String,
    val instanceName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    companion object {
        fun of(course: Course): CourseDTO = CourseDTO(
            course.canvasCourseId,
            course.courseName,
            course.instanceName,
            course.startDate,
            course.endDate
        )
    }
}