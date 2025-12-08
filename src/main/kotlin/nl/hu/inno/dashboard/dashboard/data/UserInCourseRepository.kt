package nl.hu.inno.dashboard.dashboard.data

import nl.hu.inno.dashboard.dashboard.domain.UserInCourse
import org.springframework.data.jpa.repository.JpaRepository

interface UserInCourseRepository : JpaRepository<UserInCourse, Long> {
}