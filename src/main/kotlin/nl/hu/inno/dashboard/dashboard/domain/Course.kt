package nl.hu.inno.dashboard.dashboard.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("COURSE")
data class Course(
    @Id
    @Column("CANVAS_ID")
    val canvasId: Int,

    @Column("TITLE")
    val title: String,

    @Column("COURSE_CODE")
    val courseCode: String,

    @Column("START_DATE")
    val startDate: LocalDate,

    @Column("END_DATE")
    val endDate: LocalDate)