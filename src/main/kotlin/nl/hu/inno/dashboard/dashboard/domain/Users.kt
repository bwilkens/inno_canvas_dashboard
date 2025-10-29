package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.Table
import jakarta.persistence.ManyToMany

@Entity
@Table(name = "USERS")
data class Users (
    @Id
    @Column(name = "EMAIL_ADDRESS")
    val emailAdress: String = "",

    @Column(name = "NAME")
    val name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    val role: Role? = null,

    @ManyToMany
    @JoinTable(
        name = "USER_COURSE",
        joinColumns = [JoinColumn(name = "USER_EMAIL")],
        inverseJoinColumns = [JoinColumn(name = "COURSE_CANVAS_ID")]
    )
    val courses: Set<Course> = emptySet()
)