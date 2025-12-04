package nl.hu.inno.dashboard.dashboard.presentation

import jakarta.servlet.http.HttpServletRequest
import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dashboard/")
class DashboardController(
    private val service: DashboardServiceImpl
    ) {

    @GetMapping("/users")
    fun getCurrentUser(@AuthenticationPrincipal user: OAuth2User): ResponseEntity<UsersDTO> {
        val email = user.attributes["email"] as? String
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val userDTO = service.findUserByEmail(email)
        return ResponseEntity.ok(userDTO)
    }

    @GetMapping(("/{instanceName}/**"))
    fun getDashboard(@PathVariable instanceName: String, @AuthenticationPrincipal user: OAuth2User, request: HttpServletRequest): ResponseEntity<Resource> {
        val relativeRequestPath = request.requestURI.removePrefix("/api/v1/dashboard/")

        val email = user.attributes["email"] as? String
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val resource = service.getDashboardHtml(email, instanceName, relativeRequestPath)
        return ResponseEntity.ok(resource)
    }

    @PostMapping("/internal/users/refresh")
    fun refreshUsersAndCourses(): ResponseEntity<Void> {
        service.refreshUsersAndCourses()
        return ResponseEntity.ok().build()
    }
}