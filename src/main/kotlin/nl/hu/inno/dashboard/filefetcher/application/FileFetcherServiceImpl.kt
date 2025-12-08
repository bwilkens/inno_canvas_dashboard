package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.exception.exceptions.InvalidPathException
import nl.hu.inno.dashboard.filefetcher.domain.HtmlPathResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.net.URI

@Service
@Profile("prod")
class FileFetcherServiceImpl(
    @Value("\${filefetcher.base-url}")
    private val baseUrl: String
) : FileFetcherService {

    override fun fetchCsvFile(): Resource {
        val path = "${baseUrl}/user_data.csv"
        return UrlResource(URI.create(path))
    }

    override fun fetchDashboardHtml(email: String, role: String, courseCode: String, instanceName: String, relativeRequestPath: String): Resource {
        val baseUrlWithInstance = "$baseUrl/$courseCode/$instanceName/dashboard"
        val resolvedPath = HtmlPathResolver.resolvePath(email, role, instanceName, relativeRequestPath)
        val fullPath = "$baseUrlWithInstance/$resolvedPath"

        val resource = UrlResource(URI.create(fullPath))
        if (!resource.exists()) {
            throw InvalidPathException("Path $resolvedPath did not lead to an existing resource")
        }

        return resource
    }
}