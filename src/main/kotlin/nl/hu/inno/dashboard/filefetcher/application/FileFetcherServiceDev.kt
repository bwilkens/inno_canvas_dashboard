package nl.hu.inno.dashboard.filefetcher.application

import nl.hu.inno.dashboard.filefetcher.domain.exception.InvalidPathException
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

    override fun fetchDashboardHtml(instanceName: String, fullPath: String, role: String, email: String): Resource {
        /*
        TODO:
        - separate functionality -> move logic to domain
        - ensure parity between dev and impl versions
        - move constants outside of class specific companion object depending on how the spring profiles will end up working for local/prod
        -
        - remove prints
         */
        val baseUrlWithInstance = "$baseUrl/$instanceName/dashboard_$instanceName"

        val path = when (role) {
            ROLE_TEACHER -> when {
                TEACHER_PATHS.any { fullPath.contains(it) } -> {
                    "$baseUrlWithInstance/$fullPath"
                }
                fullPath.equals(instanceName, true) -> {
                    "$baseUrlWithInstance/index.html"
                }
                else -> {
                    throw InvalidPathException("Path $fullPath did not lead to an existing resource")
                }
            }
            ROLE_STUDENT -> {
                val firstPartEmail = email.substringBefore("@")
                "$baseUrlWithInstance/$instanceName/students/${firstPartEmail}_index.html"

            }
            else -> throw InvalidRoleException("Role '$role' is not a valid role")
        }
        println("Final path: $path")
        println("_____")
        return UrlResource(URI.create(path))
    }
    companion object {
        private const val ROLE_TEACHER = "TEACHER"
        private const val ROLE_STUDENT = "STUDENT"

        private val TEACHER_PATHS = listOf(
            "/students/",
            "/totals_voortgang.html",
            "/workload_index.html",
            "overall_opbouw.html",
            "/standard.html"
        )
    }
}