package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.*

@Entity
@Table(name = "USER_COURSE")
class UserInCourse (
    @EmbeddedId
    val id: UserInCourseId = UserInCourseId(),

    @ManyToOne
    @MapsId("userEmail")
    @JoinColumn(name = "USER_EMAIL")
    val user: Users? = null,

    @ManyToOne
    @MapsId("canvasCourseId")
    @JoinColumn(name = "CANVAS_COURSE_ID")
    val course: Course? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "COURSE_ROLE")
    val courseRole: CourseRole? = null,
) {
    constructor() : this(UserInCourseId(), null, null, null)

    companion object {
        fun createAndLink(user: Users, course: Course, courseRole: CourseRole): UserInCourse {
            val link = UserInCourse(
                id = UserInCourseId(user.email, course.canvasCourseId),
                user = user,
                course = course,
                courseRole = courseRole
            )

            user.userInCourse.add(link)
            course.userInCourse.add(link)
            return link
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other ||
                (other is UserInCourse &&
                        id == other.id)
    
    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "UserInCourse(id=$id, courseRole=$courseRole)"
    }
}