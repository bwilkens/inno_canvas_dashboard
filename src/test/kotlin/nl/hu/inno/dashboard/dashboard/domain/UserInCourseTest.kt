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

        assertTrue(user.userInCourse.contains(link))
        assertTrue(course.userInCourse.contains(link))
        assertEquals(user, link.user)
        assertEquals(course, link.course)
        assertEquals(courseRole, link.courseRole)
    }

    @Test
    fun equals_returnsTrue_whenSameReference() {
        val user = Users(email = "test@hu.nl", name = "Test User")
        val course = Course(canvasCourseId = 123, courseName = "Test Course")
        val courseRole = CourseRole.STUDENT
        val link = UserInCourse.createAndLink(user, course, courseRole)

        assertTrue(link.equals(link))
    }

    @Test
    fun equals_returnsFalse_whenComparedToDifferentType() {
        val user = Users(email = "test@hu.nl", name = "Test User")
        val course = Course(canvasCourseId = 123, courseName = "Test Course")
        val courseRole = CourseRole.STUDENT
        val link = UserInCourse.createAndLink(user, course, courseRole)

        assertFalse(link.equals("not a UserInCourse"))
    }

    @Test
    fun equalsAndHashCode_workAsExpected() {
        val user = Users(email = "test@hu.nl", name = "Test User")
        val course = Course(canvasCourseId = 123, courseName = "Test Course")
        val courseRole = CourseRole.STUDENT

        val link1 = UserInCourse.createAndLink(user, course, courseRole)
        val link2 = UserInCourse(
            id = UserInCourseId("test@hu.nl", 123),
            user = user,
            course = course,
            courseRole = courseRole
        )
        val link3 = UserInCourse(
            id = UserInCourseId("other@hu.nl", 999),
            user = user,
            course = course,
            courseRole = courseRole
        )

        assertEquals(link1, link2)
        assertEquals(link1.hashCode(), link2.hashCode())
        assertNotEquals(link1, link3)
    }

    @Test
    fun toString_returnsExpectedString() {
        val user = Users(email = "test@hu.nl", name = "Test User")
        val course = Course(canvasCourseId = 123, courseName = "Test Course")
        val courseRole = CourseRole.STUDENT
        val link = UserInCourse.createAndLink(user, course, courseRole)
        val expected = "UserInCourse(id=UserInCourseId(userEmail='test@hu.nl', canvasCourseId=123), courseRole=STUDENT)"
        assertEquals(expected, link.toString())
    }
}