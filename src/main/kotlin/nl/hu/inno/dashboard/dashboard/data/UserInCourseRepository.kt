package nl.hu.inno.dashboard.dashboard.data

import nl.hu.inno.dashboard.dashboard.domain.UserInCourse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserInCourseRepository : JpaRepository<UserInCourse, Long> {
    @Modifying
    @Query("DELETE FROM UserInCourse")
    fun deleteAllUserInCourseRecords()
}