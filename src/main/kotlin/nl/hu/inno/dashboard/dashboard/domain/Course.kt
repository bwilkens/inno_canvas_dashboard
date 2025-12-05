package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "COURSE")
class Course(
    @Id
    @Column(name = "CANVAS_COURSE_ID")
    val canvasCourseId: Int = 0,

    @Column(name = "COURSE_NAME")
    val courseName: String = "",

    @Column(name = "COURSE_CODE")
    val courseCode: String = "",

    @Column(name = "INSTANCE_NAME")
    val instanceName: String = "",

    @Column(name = "START_DATE")
    val startDate: LocalDate = LocalDate.MIN,

    @Column(name = "END_DATE")
    val endDate: LocalDate = LocalDate.MIN,

    @OneToMany(mappedBy = "course")
    val userInCourse: MutableSet<UserInCourse> = mutableSetOf()
) {
    companion object {
        fun of(canvasCourseId: Int, courseName: String, courseCode: String, instanceName: String, startDate: LocalDate, endDate: LocalDate): Course {
            return Course(canvasCourseId, courseName, courseCode, instanceName, startDate, endDate)
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Course && canvasCourseId == other.canvasCourseId)

    override fun hashCode(): Int = canvasCourseId.hashCode()
}