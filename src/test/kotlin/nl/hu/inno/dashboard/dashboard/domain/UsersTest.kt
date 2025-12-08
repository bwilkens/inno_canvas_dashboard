package nl.hu.inno.dashboard.dashboard.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UsersTest {

    @Test
    fun factoryMethod_createsUserCorrectly() {
        val user = Users.of("Test@hu.nl", "Name")

        assertEquals("test@hu.nl", user.email)
        assertEquals("Name", user.name)
        assertEquals(Privileges.USER, user.privileges)
        assertTrue(user.userInCourse.isEmpty())
    }

    @Test
    fun defaultConstructor_initializesProperties() {
        val user = Users()

        assertEquals("", user.email)
        assertEquals("", user.name)
        assertEquals(Privileges.USER, user.privileges)
        assertTrue(user.userInCourse.isEmpty())
    }

    @Test
    fun equalsAndHashCode_workAsExpected() {
        val u1 = Users.of("a@hu.nl", "A")
        val u2 = Users.of("A@hu.nl", "B")
        val u3 = Users.of("b@hu.nl", "A")

        assertEquals(u1, u2)
        assertEquals(u1.hashCode(), u2.hashCode())
        assertNotEquals(u1, u3)
    }

    @Test
    fun equals_returnsTrue_whenSameReference() {
        val user = Users.of("a@hu.nl", "A")

        assertTrue(user.equals(user))
    }

    @Test
    fun equals_returnsFalse_whenComparedToDifferentType() {
        val user = Users.of("a@hu.nl", "A")

        assertFalse(user.equals("a@hu.nl"))
    }
}