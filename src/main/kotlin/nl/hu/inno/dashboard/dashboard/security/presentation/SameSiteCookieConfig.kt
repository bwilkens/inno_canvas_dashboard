package nl.hu.inno.dashboard.security.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer

@Configuration
class SameSiteCookieConfig {

    @Bean
    fun cookieSerializer(): CookieSerializer {
        val serializer = DefaultCookieSerializer()
        serializer.setSameSite("None")
        serializer.setUseSecureCookie(true)
        serializer.setCookiePath("/")
        return serializer
    }
}
