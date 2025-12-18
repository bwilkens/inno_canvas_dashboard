package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.exception.exceptions.InvalidPathException
import nl.hu.inno.dashboard.filefetcher.domain.HtmlPathResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class FileFetcherServiceImpl(
    @Value("\${volumes.path.shared-data}")
    private val pathToSharedDataVolume: String,
    @Value("\${volumes.path.shared-data.courses}")
    private val coursesDirectory: String
) : FileFetcherService {

    override fun fetchCsvFile(): Resource {
        val path = "${pathToSharedDataVolume}$coursesDirectory/user_data.csv"
        return FileSystemResource(path)
    }

    override fun fetchDashboardHtml(email: String, role: String, courseCode: String, instanceName: String, relativeRequestPath: String): Resource {
        val baseUrlWithInstance = "$pathToSharedDataVolume$coursesDirectory/$courseCode/$instanceName/dashboard"
        val resolvedPath = HtmlPathResolver.resolvePath(email, role, instanceName, relativeRequestPath)
        val fullPath = "$baseUrlWithInstance/$resolvedPath"

        val resource = FileSystemResource(fullPath)
        if (!resource.exists()) {
            throw InvalidPathException("Path $resolvedPath did not lead to an existing resource")
        }

        return resource
    }
}