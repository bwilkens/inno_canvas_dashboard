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
    @Column(name = "APP_ROLE")
    var appRole: AppRole = AppRole.USER,

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    val userInCourse: MutableSet<UserInCourse> = mutableSetOf()
) {
    companion object {
        fun of(email: String, name: String): Users {
            return Users(email.lowercase(), name, AppRole.USER)
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Users && email == other.email)

    override fun hashCode(): Int = email.hashCode()
}