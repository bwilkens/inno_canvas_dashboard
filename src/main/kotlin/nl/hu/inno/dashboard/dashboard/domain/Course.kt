package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "COURSE")
class Course(
    @Id
    @Column(name = "CANVAS_ID")
    val canvasId: Int = 0,

    @Column(name = "TITLE")
    val title: String = "",

    @Column(name = "START_DATE")
    val startDate: LocalDate = LocalDate.MIN,

    @Column(name = "END_DATE")
    val endDate: LocalDate = LocalDate.MIN,

    @ManyToMany(mappedBy = "courses", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val users: MutableSet<Users> = mutableSetOf()
) {
    companion object {
        fun of(canvasId: Int, title: String, startDate: LocalDate, endDate: LocalDate, users: MutableSet<Users> = mutableSetOf()): Course {
            return Course(canvasId, title, startDate, endDate, users)
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Course && canvasId == other.canvasId)

    override fun hashCode(): Int = canvasId.hashCode()
}