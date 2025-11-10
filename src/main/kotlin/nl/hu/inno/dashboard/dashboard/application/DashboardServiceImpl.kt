package nl.hu.inno.dashboard.dashboard.application

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
    override fun findCourseById(id: Int): Course? {
        return courseDB.findByIdOrNull(id)
    }

    override fun addUsersToCourse() {
        val resource = fileFetcherService.fetchCsvFile()
        val records = fileParserService.parseFile(resource)

        val courseCache = mutableMapOf<Int, Course>()
        val usersCache = mutableMapOf<String, Users>()
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

    private fun addNewCoursesAndUsers(
        records: List<List<String>>,
        courseCache: MutableMap<Int, Course>,
        usersCache: MutableMap<String, Users>
    ) {
        for (record in records) {
            val courseId = record[0].toInt()
            val email = record[5]
            if (email.isBlank() || email.lowercase() == "null") continue

            val course = courseCache.getOrPut(courseId) {
                courseDB.findByIdOrNull(courseId) ?: convertToCourse(record)
            }

            val user = usersCache.getOrPut(email) {
                usersDB.findByIdOrNull(email) ?: convertToUser(record)
            }

            course.users.add(user)
            user.courses.add(course)
        }
    }

    private fun convertToCourse(record: List<String>): Course {
        val canvasId = record[0].toInt()
        val title = record[1]
        val startDate = LocalDate.parse(record[2].substring(0, 10))
        val endDate = LocalDate.parse(record[3].substring(0, 10))

        return Course.of(canvasId, title, startDate, endDate)
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

    private object CsvHeaders {
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