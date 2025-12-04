package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.Role
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.exception.exceptions.InvalidRoleException
import nl.hu.inno.dashboard.exception.exceptions.UserNotFoundException
import nl.hu.inno.dashboard.exception.exceptions.UserNotInCourseException
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherService
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.springframework.core.io.Resource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class DashboardServiceImpl(
    private val courseDB: CourseRepository,
    private val usersDB: UsersRepository,
    private val fileParserService: FileParserService,
    private val fileFetcherService: FileFetcherService,
) : DashboardService {
    override fun findUserByEmail(email: String): UsersDTO {
        val user = findUserInDatabaseByEmail(email)
        return UsersDTO.of(user)
    }

    override fun getDashboardHtml(email: String, instanceName: String, relativeRequestPath: String): Resource {
        val user = findUserInDatabaseByEmail(email)

        val userInCourse = user.courses.any { it.instanceName == instanceName }
        if (!userInCourse) {
            throw UserNotInCourseException("User with email $email is not in a course with instanceName $instanceName")
        }

        val userRole = user.role.name
        return fileFetcherService.fetchDashboardHtml(email, userRole, instanceName, relativeRequestPath)
    }

    override fun refreshUsersAndCourses() {
        val resource = fileFetcherService.fetchCsvFile()
        val records = fileParserService.parseFile(resource)

        val usersCache = mutableMapOf<String, Users>()
        val courseCache = mutableMapOf<Int, Course>()
        linkUsersAndCourses(records, usersCache, courseCache)

        courseDB.deleteAll()
        usersDB.deleteAll()

        courseDB.saveAll(courseCache.values)
        usersDB.saveAll(usersCache.values)
    }

    private fun findUserInDatabaseByEmail(email: String): Users {
        val lowercaseEmail = email.lowercase()
        val user = usersDB.findByIdOrNull(lowercaseEmail)
        if (user == null) {
            throw UserNotFoundException("User with email $email not found")
        }
        return user
    }

    private fun linkUsersAndCourses(
        records: List<List<String>>,
        usersCache: MutableMap<String, Users>,
        courseCache: MutableMap<Int, Course>
    ) {
        for (record in records) {
            val email = record[CsvColumns.USER_EMAIL]
            if (email.isBlank() || email.lowercase() == "null") continue
            val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()

            val user = usersCache.getOrPut(email) { convertToUser(record) }
            val course = courseCache.getOrPut(canvasCourseId) { convertToCourse(record) }

            user.linkWithCourse(course)
        }
    }

    private fun convertToCourse(record: List<String>): Course {
        val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()
        val courseName = record[CsvColumns.COURSE_NAME]
        val instanceName = record[CsvColumns.INSTANCE_NAME]
        val startDate = LocalDate.parse(record[CsvColumns.START_DATE].substring(0, 10))
        val endDate = LocalDate.parse(record[CsvColumns.END_DATE].substring(0, 10))

        return Course.of(canvasCourseId, courseName, instanceName, startDate, endDate)
    }

    private fun convertToUser(record: List<String>): Users {
        val name = record[CsvColumns.USER_NAME]
        val email = record[CsvColumns.USER_EMAIL]
        val role = when (record[CsvColumns.USER_ROLE].uppercase()) {
            "STUDENT" -> Role.STUDENT
            "TEACHER" -> Role.TEACHER
            "ADMIN" -> Role.ADMIN
            else -> throw InvalidRoleException("Invalid role: ${record[CsvColumns.USER_ROLE]}")
        }

        return Users.of(email, name, role)
    }

    private object CsvColumns {
        const val CANVAS_COURSE_ID = 0
        const val COURSE_NAME = 1
        const val INSTANCE_NAME = 2
        const val START_DATE = 3
        const val END_DATE = 4
        const val USER_NAME = 5
        const val USER_EMAIL = 6
        const val USER_ROLE = 7
    }
}