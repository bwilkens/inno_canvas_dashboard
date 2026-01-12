package nl.hu.inno.dashboard.filefetcher.domain

import nl.hu.inno.dashboard.exception.exceptions.InvalidRoleException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HtmlPathResolverTest {
    private lateinit var resolver: HtmlPathResolver

    @BeforeEach
    fun setUp() {
        resolver = HtmlPathResolver()
    }

    @Test
    fun resolvePath_returnsIndexHtml_forTeacher_whenInstanceEqualsPath() {
        val actualPath = resolver.resolvePath(
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
        val actualPath = resolver.resolvePath(
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
        val actualPath = resolver.resolvePath(
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
        val exception = assertThrows(InvalidRoleException::class.java) {
            resolver.resolvePath(
                email = "user@hu.nl",
                role = "ADMIN",
                instanceName = "TICT-V3SE6-25_SEP25",
                relativeRequestPath = "dashboard"
            )
        }

        val expectedMessage = "Role 'ADMIN' is not a valid role"
        assertEquals(expectedMessage, exception.message)
    }
}