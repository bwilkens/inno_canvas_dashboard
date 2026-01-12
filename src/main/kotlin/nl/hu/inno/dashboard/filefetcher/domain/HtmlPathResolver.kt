package nl.hu.inno.dashboard.filefetcher.domain

import nl.hu.inno.dashboard.exception.exceptions.InvalidRoleException
import org.springframework.stereotype.Component

@Component
class HtmlPathResolver {
    fun resolvePath(email: String, role: String, instanceName: String, relativeRequestPath: String): String {
        return when (role.uppercase()) {
            ROLE_TEACHER -> when {
                instanceName.equals(other = relativeRequestPath, ignoreCase = true) -> "index.html"
                else -> relativeRequestPath
            }

            ROLE_STUDENT -> {
                val firstPartEmail = email.substringBefore("@")
                "$instanceName/students/${firstPartEmail}_index.html"
            }

            else -> throw InvalidRoleException("Role '$role' is not a valid role")
        }
    }

    companion object {
        private const val ROLE_TEACHER = "TEACHER"
        private const val ROLE_STUDENT = "STUDENT"
    }
}