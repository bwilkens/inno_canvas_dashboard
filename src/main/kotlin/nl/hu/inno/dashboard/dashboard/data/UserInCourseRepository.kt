package nl.hu.inno.dashboard.dashboard.data

import nl.hu.inno.dashboard.dashboard.domain.UserInCourse
import nl.hu.inno.dashboard.dashboard.domain.UserInCourseId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserInCourseRepository : JpaRepository<UserInCourse, UserInCourseId> {
    @Modifying
    @Query("DELETE FROM UserInCourse")
    fun deleteAllUserInCourseRecords()
}