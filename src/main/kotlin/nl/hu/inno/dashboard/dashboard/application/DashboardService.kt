package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO

interface DashboardService {
    fun findUserByEmail(email: String): UsersDTO?
    fun addUsersToCourse()
    fun updateUsersInCourse()
}