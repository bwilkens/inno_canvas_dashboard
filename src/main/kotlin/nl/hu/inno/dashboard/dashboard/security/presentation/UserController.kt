package nl.hu.inno.dashboard.dashboard.security.presentation

import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/security/users/")
class UserController(
    private val clientService: OAuth2AuthorizedClientService
) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal user: OAuth2User): Map<String, Any> {
        return user.attributes
    }

    @GetMapping("/me/token")
    fun token(authentication: Authentication): String? {
        val client: OAuth2AuthorizedClient? =
            clientService.loadAuthorizedClient(
                "azure",
                authentication.name
            )

        return client?.accessToken?.tokenValue
    }
}