package nl.hu.inno.dashboard.dashboard.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "COURSE")
data class Course(
    @Id
    @Column(name = "CANVAS_ID")
    val canvasId: Int = 0,

    @Column(name = "TITLE")
    val title: String = "",

    @Column(name = "COURSE_CODE")
    val courseCode: String = "",

    @Column(name = "START_DATE")
    val startDate: LocalDate = LocalDate.MIN,

    @Column(name = "END_DATE")
    val endDate: LocalDate = LocalDate.MIN,

    @ManyToMany(mappedBy = "courses")
    val users: Set<Users> = emptySet()
)