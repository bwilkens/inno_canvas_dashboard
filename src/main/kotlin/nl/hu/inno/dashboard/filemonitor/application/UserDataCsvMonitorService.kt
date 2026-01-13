package nl.hu.inno.dashboard.filemonitor.application

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import nl.hu.inno.dashboard.dashboard.application.DashboardService
import nl.hu.inno.dashboard.filemonitor.domain.HashChecker
import org.apache.commons.io.filefilter.NameFileFilter
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths

@Service
class UserDataCsvMonitorService(
    @Value("\${volumes.path.shared-data}")
    private val pathToSharedDataVolume: String,
    @Value("\${volumes.path.shared-data.courses}")
    private val coursesDirectory: String,
    @Value("\${file.monitor.interval.ms}")
    private val intervalInMillis: Long,
    private val dashboardService: DashboardService,
    private val hashChecker: HashChecker
) : FileMonitorService {

    private var monitor: FileAlterationMonitor? = null
    private val csvFileName = "user_data.csv"
    private val csvDirectoryPath: String = Paths.get(pathToSharedDataVolume, coursesDirectory).toString()
    private var lastHash: String? = null

//    TODO: replace println's and e.printStackTrace()'s here and in HashChecker with proper logging

    @PostConstruct
    override fun startWatching() {
        println("_____ initializing monitor on user_data.csv _____")
        val observer = createObserver()
        monitor = FileAlterationMonitor(intervalInMillis, observer)

        try {
        monitor?.start()
        println("_____ monitor successfully started _____")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @PreDestroy
    override fun stopWatching() {
        try {
            println("_____ gracefully stopping monitor on user_data.csv _____")
            monitor?.stop()
            println("_____ monitor successfully stopped _____")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createObserver(): FileAlterationObserver {
        verifyCoursesDirectoryExists()

        val observer = FileAlterationObserver.builder()
            .setPath(csvDirectoryPath)
            .setFileFilter(NameFileFilter(csvFileName))
            .get()

        observer.addListener(object : FileAlterationListenerAdaptor() {
            override fun onFileChange(file: File) = handleFileChange(file)
            override fun onFileCreate(file: File) = handleFileChange(file)
        })

        return observer
    }

    private fun handleFileChange(file: File) {
        val (changed, newHash) = hashChecker.isContentChanged(file, lastHash)
        if (changed) {
            lastHash = newHash
            dashboardService.refreshUsersAndCoursesInternal()
        }
    }

    private fun verifyCoursesDirectoryExists() {
        val dir = File(csvDirectoryPath)

        if (!dir.exists()) {
//            create the courses folder inside the shared-data volume if it does not exist
            dir.mkdirs()
        }
        if (!dir.exists() || !dir.isDirectory || !dir.canRead()) {
            throw IllegalStateException("Directory $csvDirectoryPath does not exist or is not readable.")
        }
    }
}