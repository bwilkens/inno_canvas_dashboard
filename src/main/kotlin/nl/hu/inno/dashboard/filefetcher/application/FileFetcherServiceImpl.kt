package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.exception.exceptions.InvalidPathException
import nl.hu.inno.dashboard.filefetcher.domain.HtmlPathResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.nio.file.Paths
import org.slf4j.LoggerFactory

@Service
class FileFetcherServiceImpl(
    @Value("\${volumes.path.shared-data}")
    private val pathToSharedDataVolume: String,
    @Value("\${volumes.path.shared-data.courses}")
    private val coursesDirectory: String,
    private val htmlPathResolver: HtmlPathResolver
) : FileFetcherService {
    
    override fun fetchCsvFile(): Resource {
        val path = Paths.get(pathToSharedDataVolume, coursesDirectory, "user_data.csv").toString()
        return FileSystemResource(path)
    }

    override fun fetchDashboardHtml(email: String, role: String, courseCode: String, instanceName: String, relativeRequestPath: String): Resource {
        val baseUrlWithInstance = Paths.get(pathToSharedDataVolume, coursesDirectory, courseCode, instanceName, "dashboard").toString()
        val resolvedPath = htmlPathResolver.resolvePath(email, role, instanceName, relativeRequestPath)
        val fullPath = Paths.get(baseUrlWithInstance, resolvedPath).toString()

        val resource = FileSystemResource(fullPath)
        if (!resource.exists()) {
            log.warn("Dashboard HTML not found: email={}, role={}, resolvedPath={}", email, role,resolvedPath)
            throw InvalidPathException("Path $resolvedPath did not lead to an existing resource")
        }

        return resource
    }

    companion object {
        private val log = LoggerFactory.getLogger(FileFetcherServiceImpl::class.java)
    }
}