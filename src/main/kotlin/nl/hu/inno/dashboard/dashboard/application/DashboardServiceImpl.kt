package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import nl.hu.inno.dashboard.dashboard.domain.Role

@Service
class DashboardServiceImpl(
    private val courseDB: CourseRepository,
    private val usersDB: UsersRepository,
    private val fileParserService: FileParserService
) : DashboardService {
    override fun findCourseById(id: Int): Course? {
        return courseDB.findByIdOrNull(id)
    }

    override fun parseAndPersistCanvasData(file: MultipartFile) {
        val rows = fileParserService.parseFile(file)

        rows.forEachIndexed { index, row ->
            if (row.size != 7) {
                throw IllegalArgumentException(
                    "Invalid CSV format on row ${index + 1}: expected 7 columns, found ${row.size}"
                )
            }
        }

        val courseList = rows.map { parseCourseList(it) }
        val userList = rows.map { parseUserList(it) }

        courseDB.saveAll(courseList)
        usersDB.saveAll(userList)
    }

    private fun parseUserList(columns: List<String>): Users {
        val name = columns[4]
        val emailAddress = columns[5]
        val role = when (columns[6].uppercase()) {
            "STUDENT" -> Role.STUDENT
            "TEACHER" -> Role.TEACHER
            "ADMIN" -> Role.ADMIN
            else -> throw IllegalArgumentException("Invalid role: ${columns[6]}")
        }

        return Users(
            name = name,
            emailAddress = emailAddress,
            role = role
        )
    }

    private fun parseCourseList(columns: List<String>): Course {
        val canvasId = columns[0].toInt()
        val title = columns[1]
        val courseCode = title
        val startDate = LocalDate.parse(columns[2].substring(0, 10))
        val endDate = LocalDate.parse(columns[3].substring(0, 10))

        return Course(
            canvasId = canvasId,
            title = title,
            courseCode = courseCode,
            startDate = startDate,
            endDate = endDate
        )
    }
}