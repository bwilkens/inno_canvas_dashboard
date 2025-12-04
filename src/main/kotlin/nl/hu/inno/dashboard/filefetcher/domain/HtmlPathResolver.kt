package nl.hu.inno.dashboard.filefetcher.domain

import nl.hu.inno.dashboard.exception.exceptions.InvalidPathException
import nl.hu.inno.dashboard.exception.exceptions.InvalidRoleException

object HtmlPathResolver {
    private const val ROLE_TEACHER = "TEACHER"
    private const val ROLE_STUDENT = "STUDENT"

    private val TEACHER_PATHS = listOf(
        "/students/",
        "/totals_voortgang.html",
        "/workload_index.html",
        "overall_opbouw.html",
        "/standard.html"
    )

    fun resolvePath(email: String, role: String, instanceName: String, relativeRequestPath: String): String {
        return when (role) {
            ROLE_TEACHER -> when {
                TEACHER_PATHS.any { relativeRequestPath.contains(other = it) } -> relativeRequestPath
                instanceName.equals(other = relativeRequestPath, ignoreCase = true) -> "index.html"
                else -> throw InvalidPathException("Path $relativeRequestPath did not lead to an existing resource")
            }
            ROLE_STUDENT -> {
                val firstPartEmail = email.substringBefore("@")
                "$instanceName/students/${firstPartEmail}_index.html"
            }
            else -> throw InvalidRoleException("Role '$role' is not a valid role")
        }
    }
}