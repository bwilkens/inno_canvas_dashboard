package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DashboardServiceImpl(private val courseDB: CourseRepository) : DashboardService {
    override fun findCourseById(id: Int): Course? {
        return courseDB.findByIdOrNull(id)
    }
}