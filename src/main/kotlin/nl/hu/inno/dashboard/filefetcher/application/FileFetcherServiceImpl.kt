package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.filefetcher.domain.exception.InvalidRoleException
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.net.URI

@Service
class FileFetcherServiceImpl : FileFetcherService {
    override fun fetchCsvFile(): Resource {
        TODO("Not yet implemented")
    }

    override fun fetchDashboardHtml(instanceName: String, role: String, email: String): Resource {
        val hostName = "http://localhost:5000"

        val path = when (role) {
            "ADMIN", "TEACHER" -> {
                "${hostName}/${instanceName}/dashboard_${instanceName}/index.html"
            }
            "STUDENT" -> {
                val firstPartEmail = email.substringBefore("@")
                "${hostName}/${instanceName}/dashboard_${instanceName}/${instanceName}/students/${firstPartEmail}_index.html"

            }
            else -> throw InvalidRoleException("Role '$role' is not a valid role")
        }
        return UrlResource(URI.create(path))
    }
}