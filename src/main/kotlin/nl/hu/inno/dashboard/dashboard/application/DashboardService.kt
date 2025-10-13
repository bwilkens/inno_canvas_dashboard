package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.domain.Course

interface DashboardService {
    fun findCourseById(id: Int): Course?
}