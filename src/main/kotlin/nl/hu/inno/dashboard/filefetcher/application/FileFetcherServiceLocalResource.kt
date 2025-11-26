//package nl.hu.inno.dashboard.filefetcher.application
//
//import nl.hu.inno.dashboard.filefetcher.domain.exception.InvalidRoleException
//import org.springframework.core.io.Resource
//import org.springframework.core.io.UrlResource
//import org.springframework.stereotype.Service
//import java.net.URI
//
//@Service
//class FileFetcherServiceImpl : FileFetcherService {
//    private val hostName = "http://localhost:5000"
//
//    override fun fetchCsvFile(): Resource {
//        val path = "${hostName}/user_data.csv"
//        return UrlResource(URI.create(path))
//    }
//
//    override fun fetchDashboardHtml(instanceName: String, role: String, email: String): Resource {
//        val path = when (role) {
//            "ADMIN", "TEACHER" -> {
//                "${hostName}/${instanceName}/dashboard_${instanceName}/index.html"
//            }
//            "STUDENT" -> {
//                val firstPartEmail = email.substringBefore("@")
//                "${hostName}/${instanceName}/dashboard_${instanceName}/${instanceName}/students/${firstPartEmail}_index.html"
//
//            }
//            else -> throw InvalidRoleException("Role '$role' is not a valid role")
//        }
//        return UrlResource(URI.create(path))
//    }
//}

// TODO: implement @profile annotation and make a dev and prod implementation with application.properties and updated deployment to use the correct profile
// also turn hostName into variable with @value annotation and use different values in prod and dev application.properties