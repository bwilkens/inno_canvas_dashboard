package nl.hu.inno.dashboard.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    @Value("\${azure.tenant-id}")
    private val tenantId: String
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { }
            .oauth2ResourceServer { resourceServer ->
                resourceServer.jwt { }
            }
            .csrf { csrf -> csrf.disable() }
            .headers { headers -> headers.frameOptions { it.disable() }}
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val issuer = "https://login.microsoftonline.com/$tenantId/v2.0"
        return JwtDecoders.fromIssuerLocation(issuer)
    }
}