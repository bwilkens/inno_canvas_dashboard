package nl.hu.inno.dashboard.dashboard.data

import nl.hu.inno.dashboard.dashboard.domain.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
}