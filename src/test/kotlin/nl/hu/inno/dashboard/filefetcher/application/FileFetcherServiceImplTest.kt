package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.exception.exceptions.InvalidPathException
import nl.hu.inno.dashboard.filefetcher.domain.HtmlPathResolver
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.core.io.FileSystemResource
import java.io.File

class FileFetcherServiceImplTest {
    private val pathToSharedDataVolume = "/tmp/"
    private val coursesDirectory = "courses/"
    private lateinit var htmlPathResolver: HtmlPathResolver
    private lateinit var service: FileFetcherServiceImpl

    @BeforeEach
    fun setUp() {
        htmlPathResolver = mock(HtmlPathResolver::class.java)
        service = FileFetcherServiceImpl(pathToSharedDataVolume, coursesDirectory, htmlPathResolver)
    }

    @Test
    fun fetchCsvFile_returnsResourceWithCorrectPath() {
        val expectedPath = "/tmp/courses/user_data.csv"
        val resource = service.fetchCsvFile()
        assertTrue(resource is FileSystemResource)
        assertEquals(expectedPath, (resource as FileSystemResource).path)
    }

    @Test
    fun fetchDashboardHtml_returnsResource_whenFileExists() {
        val email = "user@hu.nl"
        val role = "USER"
        val courseCode = "C1"
        val instanceName = "2025"
        val relativeRequestPath = "index.html"
        val baseDir = "/tmp/courses/C1/2025/dashboard"
        val resolvedPath = "index.html"
        val fullPath = "$baseDir/$resolvedPath"

        `when`(htmlPathResolver.resolvePath(email, role, instanceName, relativeRequestPath)).thenReturn(resolvedPath)

        val file = File(fullPath)
        file.parentFile.mkdirs()
        file.writeText("test")

        val resource = service.fetchDashboardHtml(email, role, courseCode, instanceName, relativeRequestPath)
        assertTrue(resource.exists())
        assertEquals(fullPath, (resource as FileSystemResource).path)

        file.delete()
        file.parentFile.delete()
    }

    @Test
    fun fetchDashboardHtml_throwsInvalidPathException_whenFileDoesNotExist() {
        val email = "user@hu.nl"
        val role = "USER"
        val courseCode = "C1"
        val instanceName = "2025"
        val relativeRequestPath = "missing.html"
        val resolvedPath = "missing.html"

        `when`(htmlPathResolver.resolvePath(email, role, instanceName, relativeRequestPath)).thenReturn(resolvedPath)

        val exception = assertThrows(InvalidPathException::class.java) {
            service.fetchDashboardHtml(email, role, courseCode, instanceName, relativeRequestPath)
        }
        assertEquals("Path $resolvedPath did not lead to an existing resource", exception.message)
    }
}