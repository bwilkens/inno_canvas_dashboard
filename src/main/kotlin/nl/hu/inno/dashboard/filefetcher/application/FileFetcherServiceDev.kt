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

    override fun fetchDashboardHtml(instanceName: String, fullPath: String, role: String, email: String): Resource {
        /*
        TODO:
        - remove admin role
        - make sure path filtering on /students/  and /general/ always goes well for teacher dashboard
        - separate functionality -> move logic to domain
        - ensure parity between dev and impl versions
        - figure out if dev/prod profiles work on domain somehow?
        - consolidate functions in `when` where possible and replace final `else` with an exception
        - create constants to use instead of hardcoding strings (see if they can be maintained outside the implementation to prevent duplication
        -
        - look into difference between current implementation and using rest templates -> which is better in our use-case?
        - add necessary exceptions if fetch fails -> especially important if using rest template and get != status 200
        -
        - clean up code, for example, there is now a baseUrl and baseUrlWithInstance
        - also the adjustedPath, figure out how to best clean up these variables and decrease duplication
        - remove prints
        -
        - consider consolidating the instanceName and fullPath variables this function receives from the dashboard controller
        - (and refactor entire chain if necessary)
         */
        println("[FILEFETCHER] Instance name: $instanceName and fullPath: $fullPath")
        val adjustedPath = instanceName + fullPath.substringAfterLast(instanceName)
        println("adjustedPath: $adjustedPath")

        val baseUrlWithInstance = "$baseUrl/$instanceName/dashboard_$instanceName"
        
        val path = when (role) {
            "ADMIN", "TEACHER" -> when {

                fullPath.contains("/students/") -> {
                    println("ran /students/ if")
                    "${baseUrlWithInstance}/${adjustedPath}"
                }
                fullPath.contains("/totals_voortgang.html") -> {
                    println("ran /totals_voortgang if")
                    "${baseUrlWithInstance}/${adjustedPath}"
                }
                fullPath.contains("/workload_index.html") -> {
                    println("ran /workload_index if")
                    "${baseUrlWithInstance}/${adjustedPath}"
                }
                fullPath.contains("/overall_opbouw.html") -> {
                    println("ran /overall_opbouw if")
                    "${baseUrlWithInstance}/${adjustedPath}"
                }
                fullPath.equals(instanceName, true) -> {
                    println("ran second if")
                    "${baseUrlWithInstance}/index.html"
                }
                fullPath.contains("/standard.html") -> {
                    println("ran /standard.html")
                    "${baseUrlWithInstance}/${adjustedPath}"
                }
                else -> {
                    println("ran final else")
                    baseUrl
                }
            }
            "STUDENT" -> {
                val firstPartEmail = email.substringBefore("@")
                "${baseUrl}/${instanceName}/dashboard_${instanceName}/${instanceName}/students/${firstPartEmail}_index.html"

            }
            else -> throw InvalidRoleException("Role '$role' is not a valid role")
        }
        println("Final path: $path")
        println("_____")
        return UrlResource(URI.create(path))
    }
}