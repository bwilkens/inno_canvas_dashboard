package nl.hu.inno.dashboard.dashboard.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CourseTest {

    @Test
    fun factoryMethod_createsCourseCorrectly() {
        val course = Course.of(1, "Test", "Instance", LocalDate.of(2025,1,1), LocalDate.of(2025,12,31))

        assertEquals(1, course.canvasCourseId)
        assertEquals("Test", course.courseName)
        assertEquals("Instance", course.instanceName)
        assertEquals(LocalDate.of(2025,1,1), course.startDate)
        assertEquals(LocalDate.of(2025,12,31), course.endDate)
        assertTrue(course.users.isEmpty())
    }

    @Test
    fun defaultConstructor_initializesProperties() {
        val course = Course()

        assertEquals(0, course.canvasCourseId)
        assertEquals("", course.courseName)
        assertEquals("", course.instanceName)
        assertEquals(LocalDate.MIN, course.startDate)
        assertEquals(LocalDate.MIN, course.endDate)
        assertTrue(course.users.isEmpty())
    }

    @Test
    fun equalsAndHashCode_workAsExpected() {
        val c1 = Course.of(1, "A", "B", LocalDate.MIN, LocalDate.MIN)
        val c2 = Course.of(1, "X", "Y", LocalDate.MAX, LocalDate.MAX)
        val c3 = Course.of(2, "A", "B", LocalDate.MIN, LocalDate.MIN)

        assertEquals(c1, c2)
        assertEquals(c1.hashCode(), c2.hashCode())
        assertNotEquals(c1, c3)
    }

    @Test
    fun equals_returnsTrue_whenSameReference() {
        val course = Course.of(1, "Test", "Instance", LocalDate.of(2025,1,1), LocalDate.of(2025,12,31))

        assertTrue(course.equals(course))
    }

    @Test
    fun equals_returnsFalse_whenComparedToDifferentType() {
        val course = Course.of(1, "Test", "Instance", LocalDate.of(2025,1,1), LocalDate.of(2025,12,31))

        assertFalse(course.equals("not a course"))
    }
}