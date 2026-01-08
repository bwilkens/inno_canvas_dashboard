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
    private val dashboardService: DashboardService,
    private val hashChecker: HashChecker
) : FileMonitorService {

    private lateinit var monitor: FileAlterationMonitor
    private val csvFileName = "user_data.csv"
    private val csvDirectoryPath: String = Paths.get(pathToSharedDataVolume, coursesDirectory).toString()

//    TODO: remove println's here and in HashChecker component

    @PostConstruct
    override fun startWatching() {
        println("_____ initializing monitor _____")
        val observer = createObserver()
        val intervalInMillis: Long = 5000
        monitor = FileAlterationMonitor(intervalInMillis, observer)

        monitor.start()
        println("_____ monitor successfully started _____")
    }

    @PreDestroy
    override fun stopWatching() {
        println("_____ stopping monitor _____")
        monitor.stop()
        println("_____ monitor successfully stopped _____")
    }

    private fun createObserver(): FileAlterationObserver {
        println("_____ creating observer _____")
        val observer = FileAlterationObserver.builder()
            .setPath(csvDirectoryPath)
            .setFileFilter(NameFileFilter(csvFileName))
            .get()

        observer.addListener(object : FileAlterationListenerAdaptor() {
            override fun onFileChange(file: File) = handleFileChange(file)
        })

        println("_____ successfully created observer _____")

        return observer
    }

    private fun handleFileChange(file: File) {
        if (hashChecker.isContentChanged(file)) {
            println("_____ running handleFileChange -> detected change and hash difference _____")
        }
    }
}