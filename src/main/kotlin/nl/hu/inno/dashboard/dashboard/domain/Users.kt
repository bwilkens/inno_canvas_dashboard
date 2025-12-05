package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.*

@Entity
@Table(name = "USERS")
class Users (
    @Id
    @Column(name = "EMAIL")
    val email: String = "",

    @Column(name = "NAME")
    val name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "PRIVILEGES")
    val privileges: Privileges = Privileges.USER,

    @OneToMany(mappedBy = "user")
    val userInCourse: MutableSet<UserInCourse> = mutableSetOf()
) {
    companion object {
        fun of(email: String, name: String): Users {
            return Users(email.lowercase(), name, Privileges.USER)
        }
    }

    fun linkWithCourse(course: Course, courseRole: CourseRole) : UserInCourse {
        val link = UserInCourse(user = this, course = course, courseRole = courseRole)
        this.userInCourse.add(link)
        course.userInCourse.add(link)

        return link
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Users && email == other.email)

    override fun hashCode(): Int = email.hashCode()
}