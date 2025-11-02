package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.domain.Course
import org.springframework.web.multipart.MultipartFile

interface DashboardService {
    fun findCourseById(id: Int): Course?
    fun updateUsersInCourse(file: MultipartFile)
    fun replaceUsersInCourse(file: MultipartFile)
}