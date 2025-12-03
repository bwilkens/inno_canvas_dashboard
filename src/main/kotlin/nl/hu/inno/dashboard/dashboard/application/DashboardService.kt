package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import org.springframework.core.io.Resource

interface DashboardService {
    fun findUserByEmail(email: String): UsersDTO
    fun getDashboardHtml(email: String, instanceName: String, fullPath: String): Resource
    fun refreshUsersAndCourses()
}