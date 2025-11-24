package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dashboard/")
class DashboardController(
    private val service: DashboardServiceImpl
    ) {

    @GetMapping("/users/")
    fun getCurrentUser(@AuthenticationPrincipal user: OAuth2User): ResponseEntity<UsersDTO> {
        val email = (user.attributes["email"] as? String)?.lowercase()
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val userDTO = service.findUserByEmail(email)
        return if (userDTO != null) {
            ResponseEntity.ok(userDTO)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/internal/users/new")
    fun addUsersAndCourses(): ResponseEntity<Void> {
        return try {
            service.addUsersToCourse()
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/internal/users/update")
    fun updateUsersAndCourses(): ResponseEntity<Void> {
        return try {
            service.updateUsersInCourse()
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}