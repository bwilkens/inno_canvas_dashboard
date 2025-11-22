package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.Role
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherService
import nl.hu.inno.dashboard.fileparser.application.FileParserService
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
    override fun findUserByEmail(email: String): UsersDTO? {
        val user = usersDB.findByIdOrNull(email)
        return UsersDTO.of(user)
    }

    override fun addUsersToCourse() {
        val resource = fileFetcherService.fetchCsvFile()
        val records = fileParserService.parseFile(resource)

        val courseIds = extractCourseIdsFrom(records)
        val emails = extractUserEmailsFrom(records)
        val courseCache = fillCourseCacheWithExistingCourses(courseIds)
        val usersCache = fillUsersCacheWithExistingUsers(emails)
        addNewCoursesAndUsers(records, courseCache, usersCache)

        courseDB.saveAll(courseCache.values)
        usersDB.saveAll(usersCache.values)
    }

    override fun updateUsersInCourse() {
        TODO("Not yet implemented")
        // call to new integration component to fetch csv file

        // call to fileParserServic to read MultiPartFile and return List<List<String>> data

        // update associations between users and course (add AND remove associations for users and courses)

        // persist changes in courses, users and their associations
    }

    private fun extractCourseIdsFrom(records: List<List<String>>): Set<Int> =
        records.map { it[CsvColumns.CANVAS_COURSE_ID].toInt() }.toSet()

    private fun extractUserEmailsFrom(records: List<List<String>>): Set<String> =
        records.map { it[CsvColumns.USER_EMAIL] }
            .filter { it.isNotBlank() && it.lowercase() != "null" }
            .toSet()

    private fun fillCourseCacheWithExistingCourses(courseIds: Set<Int>): MutableMap<Int, Course> =
        courseDB.findAllById(courseIds).associateBy { it.canvasCourseId }.toMutableMap()

    private fun fillUsersCacheWithExistingUsers(emails: Set<String>): MutableMap<String, Users> =
        usersDB.findAllById(emails).associateBy { it.email }.toMutableMap()

    private fun addNewCoursesAndUsers(
        records: List<List<String>>,
        courseCache: MutableMap<Int, Course>,
        usersCache: MutableMap<String, Users>
    ) {
        for (record in records) {
            val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()
            val email = record[CsvColumns.USER_EMAIL]
            if (email.isBlank() || email.lowercase() == "null") continue

            val course = courseCache.getOrPut(canvasCourseId) {
                convertToCourse(record)
            }

            val user = usersCache.getOrPut(email) {
                convertToUser(record)
            }

            course.users.add(user)
            user.courses.add(course)
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
            else -> throw IllegalArgumentException("Invalid role: ${record[6]}")
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