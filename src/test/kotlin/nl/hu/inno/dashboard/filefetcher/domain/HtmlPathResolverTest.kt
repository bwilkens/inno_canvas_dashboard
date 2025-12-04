package nl.hu.inno.dashboard.filefetcher.domain

import nl.hu.inno.dashboard.exception.exceptions.InvalidRoleException
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class HtmlPathResolverTest {
    @Test
    fun resolvePath_returnsIndexHtml_forTeacher_whenInstanceEqualsPath() {
        val actualPath = HtmlPathResolver.resolvePath(
            email = "docent@hu.nl",
            role = "TEACHER",
            instanceName = "TICT-V3SE6-25_SEP25",
            relativeRequestPath = "TICT-V3SE6-25_SEP25"
        )

        val expectedPath = "index.html"
        assertEquals(expectedPath, actualPath)
    }

    @Test
    fun resolvePath_returnsRelativePath_forTeacher_whenInstanceNotEqualsPath() {
        val actualPath = HtmlPathResolver.resolvePath(
            email = "docent@hu.nl",
            role = "TEACHER",
            instanceName = "TICT-V3SE6-25_SEP25",
            relativeRequestPath = "dashboard"
        )

        val expectedPath = "dashboard"
        assertEquals(expectedPath, actualPath)
    }

    @Test
    fun resolvePath_returnsStudentPath_forStudent() {
        val actualPath = HtmlPathResolver.resolvePath(
            email = "student123@hu.nl",
            role = "STUDENT",
            instanceName = "TICT-V3SE6-25_SEP25",
            relativeRequestPath = "dashboard"
        )

        val expectedPath = "TICT-V3SE6-25_SEP25/students/student123_index.html"
        assertEquals(expectedPath, actualPath)
    }

    @Test
    fun resolvePath_throwsException_forInvalidRole() {
        val actualMessage = assertThrows(InvalidRoleException::class.java) {
            HtmlPathResolver.resolvePath(
                email = "user@hu.nl",
                role = "ADMIN",
                instanceName = "TICT-V3SE6-25_SEP25",
                relativeRequestPath = "dashboard"
            )
        }
        
        val expectedMessage = "Role 'ADMIN' is not a valid role"
        assertEquals(expectedMessage, actualMessage.message)
    }
}