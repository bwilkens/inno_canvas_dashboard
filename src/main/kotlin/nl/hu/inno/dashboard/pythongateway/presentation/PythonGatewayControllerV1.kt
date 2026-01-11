package nl.hu.inno.dashboard.pythongateway.presentation

import nl.hu.inno.dashboard.pythongateway.application.PythonGatewayService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/scripts/")
class PythonGatewayControllerV1(
    private val service: PythonGatewayService,
) {

    @PostMapping(("/{environment}/"))
    fun getDashboard(@PathVariable environment: String, @AuthenticationPrincipal user: OAuth2User): ResponseEntity<Void> {
        val email = user.attributes["email"] as? String
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        service.startPythonScript(email, environment)

        return ResponseEntity.ok().build()
    }
}