package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.application.dto.AdminDTO
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import org.springframework.core.io.Resource

interface DashboardService {
    fun findUserByEmail(email: String): UsersDTO
    fun findAllAdmins(email: String): List<AdminDTO>
    fun updateAdminUsers(email: String, usersToUpdate: List<AdminDTO>): List<AdminDTO>
    fun getDashboardHtml(email: String, instanceName: String, relativeRequestPath: String): Resource
    fun refreshUsersAndCourses()
}