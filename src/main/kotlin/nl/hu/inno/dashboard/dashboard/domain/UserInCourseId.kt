package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class UserInCourseId(
    val userEmail: String = "",
    val canvasCourseId: Int = 0
): Serializable {
    override fun equals(other: Any?): Boolean =
        this === other ||
                (other is UserInCourseId &&
                        userEmail == other.userEmail &&
                        canvasCourseId == other.canvasCourseId)

    override fun hashCode(): Int =
        userEmail.hashCode() * 31 + canvasCourseId.hashCode()

    override fun toString(): String {
        return "UserInCourseId(userEmail='$userEmail', canvasCourseId=$canvasCourseId)"
    }
}