package nl.hu.inno.dashboard.filemonitor.application

import nl.hu.inno.dashboard.dashboard.application.DashboardService
import nl.hu.inno.dashboard.filemonitor.domain.HashChecker
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.File
import java.lang.reflect.InvocationTargetException

class UserDataCsvMonitorServiceTest {
    private lateinit var dashboardService: DashboardService
    private lateinit var hashChecker: HashChecker
    private lateinit var service: UserDataCsvMonitorService

    private val pathToSharedDataVolume = "/tmp"
    private val coursesDirectory = "courses"
    private val intervalInMillis = 1000L

    @BeforeEach
    fun setUp() {
        dashboardService = mock(DashboardService::class.java)
        hashChecker = mock(HashChecker::class.java)
        service = UserDataCsvMonitorService(
            pathToSharedDataVolume,
            coursesDirectory,
            intervalInMillis,
            dashboardService,
            hashChecker
        )
    }

    @Test
    fun startWatching_startsMonitor_whenDirectoryExists() {
        val dir = File("$pathToSharedDataVolume/$coursesDirectory")
        dir.mkdirs()
        mock(FileAlterationMonitor::class.java)
        mock(FileAlterationObserver::class.java)

        assertDoesNotThrow { service.startWatching() }
        dir.delete()
    }

    @Test
    fun stopWatching_stopsMonitorGracefully() {
        val monitor = mock(FileAlterationMonitor::class.java)
        val monitorField = UserDataCsvMonitorService::class.java.getDeclaredField("monitor")
        monitorField.isAccessible = true
        monitorField.set(service, monitor)

        assertDoesNotThrow { service.stopWatching() }
        verify(monitor).stop()
    }

    @Test
    fun verifyCoursesDirectoryExists_throwsException_whenDirectoryDoesNotExistAfterCreation() {
        val dirPath = "$pathToSharedDataVolume/$coursesDirectory"
        val dir = File(dirPath)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
        dir.parentFile.mkdirs()
        dir.createNewFile()

        val method = UserDataCsvMonitorService::class.java.getDeclaredMethod("verifyCoursesDirectoryExists")
        method.isAccessible = true

        val exception = assertThrows(InvocationTargetException::class.java) {
            method.invoke(service)
        }
        assertTrue(exception.cause is IllegalStateException)
        dir.delete()
    }

    @Test
    fun verifyCoursesDirectoryExists_createsDirectory_whenMissing() {
        val dirPath = "$pathToSharedDataVolume/$coursesDirectory"
        val dir = File(dirPath)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
        assertFalse(dir.exists())

        val method = UserDataCsvMonitorService::class.java.getDeclaredMethod("verifyCoursesDirectoryExists")
        method.isAccessible = true

        assertDoesNotThrow { method.invoke(service) }
        assertTrue(dir.exists())
        assertTrue(dir.isDirectory)
        dir.deleteRecursively()
    }

    @Test
    fun handleFileChange_callsRefresh_whenContentChanged() {
        val file = mock(File::class.java)
        val method = UserDataCsvMonitorService::class.java.getDeclaredMethod("handleFileChange", File::class.java)
        method.isAccessible = true
        `when`(hashChecker.isContentChanged(file, null)).thenReturn(Pair(true, "newHash"))

        method.invoke(service, file)
        verify(dashboardService).refreshUsersAndCoursesInternal()
    }

    @Test
    fun handleFileChange_doesNotCallRefresh_whenContentNotChanged() {
        val file = mock(File::class.java)
        val method = UserDataCsvMonitorService::class.java.getDeclaredMethod("handleFileChange", File::class.java)
        method.isAccessible = true
        `when`(hashChecker.isContentChanged(file, null)).thenReturn(Pair(false, "sameHash"))

        method.invoke(service, file)
        verify(dashboardService, never()).refreshUsersAndCoursesInternal()
    }
}