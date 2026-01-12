package nl.hu.inno.dashboard.dashboard.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserInCourseIdTest {
    @Test
    fun defaultConstructor_initializesProperties() {
        val id = UserInCourseId()
        assertEquals("", id.userEmail)
        assertEquals(0, id.canvasCourseId)
    }

    @Test
    fun equals_returnsTrue_whenSameReference() {
        val id = UserInCourseId("test@hu.nl", 123)
        assertTrue(id.equals(id))
    }

    @Test
    fun equals_returnsFalse_whenComparedToDifferentType() {
        val id = UserInCourseId("test@hu.nl", 123)
        assertFalse(id.equals("not an id"))
    }

    @Test
    fun equalsAndHashCode_workAsExpected() {
        val id1 = UserInCourseId("test@hu.nl", 123)
        val id2 = UserInCourseId("test@hu.nl", 123)
        val id3 = UserInCourseId("other@hu.nl", 999)

        assertEquals(id1, id2)
        assertEquals(id1.hashCode(), id2.hashCode())
        assertNotEquals(id1, id3)
    }

    @Test
    fun toString_returnsExpectedString() {
        val id = UserInCourseId("test@hu.nl", 123)
        val expected = "UserInCourseId(userEmail='test@hu.nl', canvasCourseId=123)"
        assertEquals(expected, id.toString())
    }
}