package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.*

@Entity
@Table(name = "USER_COURSE", uniqueConstraints = [UniqueConstraint(columnNames = ["USER_EMAIL", "CANVAS_COURSE_ID"])])
class UserInCourse (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "USER_EMAIL")
    val user: Users? = null,

    @ManyToOne
    @JoinColumn(name = "CANVAS_COURSE_ID")
    val course: Course? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "COURSE_ROLE")
    val courseRole: CourseRole? = null,
) {
    constructor() : this(0, null, null, null)

    companion object {
        fun createAndLink(user: Users, course: Course, courseRole: CourseRole): UserInCourse {
            val link = UserInCourse(user = user, course = course, courseRole = courseRole)
            user.userInCourse.add(link)
            course.userInCourse.add(link)
            return link
        }
    }
}