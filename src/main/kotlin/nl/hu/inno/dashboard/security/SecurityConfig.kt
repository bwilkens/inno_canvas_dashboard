package nl.hu.inno.dashboard.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.web.SecurityFilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
class SecurityConfig(
    @Value("\${azure.tenant-id}")
    private val tenantId: String,

    @Value("\${app.home-frontend-redirect-url}")
    private val homeRedirectUrl: String
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { login ->
                login.successHandler(oauth2SuccessHandler())
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl(
                        "https://login.microsoftonline.com/common/oauth2/v2.0/logout" +
                                "?post_logout_redirect_uri=$homeRedirectUrl"
                    )
            }
            .oauth2ResourceServer { resourceServer ->
                resourceServer.jwt { }
            }
            .csrf { csrf -> csrf.disable() }
            .headers { headers -> headers.frameOptions { it.disable() } }

        return http.build()
    }

    @Bean
    fun oauth2SuccessHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { request: HttpServletRequest,
                                              response: HttpServletResponse,
                                              authentication: Authentication ->

            val redirectUrl = request.session.getAttribute("redirect_uri") as? String
            request.session.removeAttribute("redirect_uri")

            val target = redirectUrl ?: homeRedirectUrl

            response.sendRedirect(target)
        }
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val issuer = "https://login.microsoftonline.com/$tenantId/v2.0"
        return JwtDecoders.fromIssuerLocation(issuer)
    }
}