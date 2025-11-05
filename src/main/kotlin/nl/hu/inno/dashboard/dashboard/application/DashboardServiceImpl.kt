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
import nl.hu.inno.dashboard.dashboard.domain.exception.InvalidParseListException
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DashboardServiceImpl(
    private val courseDB: CourseRepository,
    private val usersDB: UsersRepository,
    private val fileParserService: FileParserService
) : DashboardService {
    override fun findCourseById(id: Int): Course? {
        return courseDB.findByIdOrNull(id)
    }

    override fun addUsersToCourse() {
        TODO("Not yet implemented")
    }

    override fun updateUsersInCourse() {
        TODO("Not yet implemented")
    }

    private fun convertToUser(record: List<String>): Users {
        val name = record[4]
        val emailAddress = record[5]
        val role = when (record[6].uppercase()) {
            "STUDENT" -> Role.STUDENT
            "TEACHER" -> Role.TEACHER
            "ADMIN" -> Role.ADMIN
            else -> throw IllegalArgumentException("Invalid role: ${record[6]}")
        }

        return Users.of(emailAddress, name, role)
    }

    private fun convertToCourse(record: List<String>): Course {
        val canvasId = record[0].toInt()
        val title = record[1]
        val startDate = LocalDate.parse(record[2].substring(0, 10))
        val endDate = LocalDate.parse(record[3].substring(0, 10))

        return Course.of(canvasId, title, startDate, endDate)
    }

    private fun retrieveOrCreateCourse(records: List<List<String>>): Course {
        val firstRecord = records[0]
        val courseId = firstRecord[0].toInt()

        return courseDB.findByIdOrNull(courseId) ?: convertToCourse(firstRecord)
    }
}