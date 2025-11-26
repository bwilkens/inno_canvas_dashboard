package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.filefetcher.domain.exception.InvalidRoleException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.net.URI

@Service
@Profile("dev")
class FileFetcherServiceDev(
    @Value("\${filefetcher.base-url}")
    private val baseUrl: String
) : FileFetcherService {

    override fun fetchCsvFile(): Resource {
        val path = "${baseUrl}/user_data.csv"
        return UrlResource(URI.create(path))
    }

    override fun fetchDashboardHtml(instanceName: String, role: String, email: String): Resource {
        val path = when (role) {
            "ADMIN", "TEACHER" -> {
                "${baseUrl}/${instanceName}/dashboard_${instanceName}/index.html"
            }
            "STUDENT" -> {
                val firstPartEmail = email.substringBefore("@")
                "${baseUrl}/${instanceName}/dashboard_${instanceName}/${instanceName}/students/${firstPartEmail}_index.html"

            }
            else -> throw InvalidRoleException("Role '$role' is not a valid role")
        }
        return UrlResource(URI.create(path))
    }
}