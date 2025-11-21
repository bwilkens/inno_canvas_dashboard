package nl.hu.inno.dashboard.dashboard.presentation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthRedirectController {

    @GetMapping("/auth/save-redirect")
    fun saveRedirect(@RequestParam url: String, request: HttpServletRequest) {
        request.session.setAttribute("redirect_uri", url)
    }
}
