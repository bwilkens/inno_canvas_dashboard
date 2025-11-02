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

    override fun parseAndPersistCanvasData(file: MultipartFile) {
        val records = fileParserService.parseFile(file)

        val courseCache = mutableMapOf<Int, Course>()
        val userCache = mutableMapOf<String, Users>()
        processRecordsAndBuildCaches(records, courseCache, userCache)

        courseDB.saveAll(courseCache.values)
        usersDB.saveAll(userCache.values)
    }

    private fun processRecordsAndBuildCaches(
        records: List<List<String>>,
        courseCache: MutableMap<Int, Course>,
        userCache: MutableMap<String, Users>
    ) {
        for (record in records) {
            if (record.size != 7) {
                throw InvalidParseListException("Expected record to have 7 columns, got ${record.size}")
            }

            val courseId = record[0].toInt()
            val userEmail = record[5]

            val course = courseCache.getOrPut(courseId) {
                courseDB.findByIdOrNull(courseId) ?: convertToCourse(record)
            }

            val user = userCache.getOrPut(userEmail) {
                usersDB.findByIdOrNull(userEmail) ?: convertToUser(record)
            }

            val updatedUser = user.copy(courses = user.courses + course)
            val updatedCourse = course.copy(users = course.users + user)

            userCache[userEmail] = updatedUser
            courseCache[courseId] = updatedCourse
        }
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

        return Users(
            name = name,
            emailAddress = emailAddress,
            role = role
        )
    }

    private fun convertToCourse(record: List<String>): Course {
        val canvasId = record[0].toInt()
        val title = record[1]
        val startDate = LocalDate.parse(record[2].substring(0, 10))
        val endDate = LocalDate.parse(record[3].substring(0, 10))

        return Course(
            canvasId = canvasId,
            title = title,
            startDate = startDate,
            endDate = endDate
        )
    }
}