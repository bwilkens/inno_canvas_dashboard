package nl.hu.inno.dashboard.dashboard.security.presentation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/security/redirect/")
class AuthRedirectController {

    @PostMapping
    fun saveRedirect(@RequestParam url: String, request: HttpServletRequest) {
        request.session.setAttribute("redirect_uri", url)
    }
}