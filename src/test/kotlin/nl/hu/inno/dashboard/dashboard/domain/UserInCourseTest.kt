package nl.hu.inno.dashboard.dashboard.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserInCourseTest {
    @Test
    fun createAndLink_addsLinkToBothUserAndCourse() {
        val user = Users(email = "test@hu.nl", name = "Test User")
        val course = Course(canvasCourseId = 123, courseName = "Test Course")
        val courseRole = CourseRole.STUDENT

        val link = UserInCourse.createAndLink(user, course, courseRole)

        assertTrue(user.userInCourse.contains(link), "Link should be added to user")
        assertTrue(course.userInCourse.contains(link), "Link should be added to course")
        assertEquals(user, link.user)
        assertEquals(course, link.course)
        assertEquals(courseRole, link.courseRole)
    }
}