package nl.hu.inno.dashboard.dashboard.presentation

import jakarta.servlet.http.HttpServletRequest
import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.AdminDTO
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.presentation.dto.UserPutRequest
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dashboard/")
class V1DashboardController(
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

    @GetMapping("/users/admin")
    fun getAllAdminUsers(@AuthenticationPrincipal user: OAuth2User): ResponseEntity<List<AdminDTO>> {
//        SUPERADMIN only function
        val email = user.attributes["email"] as? String
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val userDTO = service.findAllAdmins(email)
        return ResponseEntity.ok(userDTO)
    }

    @PutMapping("/users/admin")
    fun updateAdminRoles(@AuthenticationPrincipal user: OAuth2User, @RequestBody updatedUsers: List<UserPutRequest>): ResponseEntity<List<AdminDTO>> {
//        SUPERADMIN only function
        val email = user.attributes["email"] as? String
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val usersToUpdate = updatedUsers.map { AdminDTO(it.email, it.name, it.appRole) }
        val userDTO = service.updateAdminUsers(email, usersToUpdate)
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

    @PostMapping("/users/refresh")
    fun refreshUsersAndCourses(@AuthenticationPrincipal user: OAuth2User): ResponseEntity<Void> {
//        ADMIN and SUPERADMIN only function
        val email = user.attributes["email"] as? String
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        service.refreshUsersAndCoursesWithRoleCheck(email)
        return ResponseEntity.ok().build()
    }
}