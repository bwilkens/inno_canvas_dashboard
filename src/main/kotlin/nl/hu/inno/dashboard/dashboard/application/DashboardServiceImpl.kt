package nl.hu.inno.dashboard.dashboard.application

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import nl.hu.inno.dashboard.dashboard.application.dto.AdminDTO
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UserInCourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.CourseRole
import nl.hu.inno.dashboard.dashboard.domain.AppRole
import nl.hu.inno.dashboard.dashboard.domain.UserInCourse
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.exception.exceptions.CourseNotFoundException
import nl.hu.inno.dashboard.exception.exceptions.InvalidRoleException
import nl.hu.inno.dashboard.exception.exceptions.UserNotAuthorizedException
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
    private val userInCourseDB: UserInCourseRepository,
    private val fileParserService: FileParserService,
    private val fileFetcherService: FileFetcherService,
    @PersistenceContext private val entityManager: EntityManager,
) : DashboardService {
    override fun findUserByEmail(email: String): UsersDTO {
        val user = findUserInDatabaseByEmail(email)
        return UsersDTO.of(user)
    }

    override fun findAllAdmins(email: String): List<AdminDTO> {
        verifyUserIsSuperAdmin(email)

        val adminEmailSuffix = "@hu.nl"
        val adminRoles = listOf(AppRole.ADMIN, AppRole.SUPERADMIN)
        val adminList = usersDB.findAllAdminCandidates(adminRoles, adminEmailSuffix)

        return adminList.map { AdminDTO.of(it) }
    }

    override fun updateAdminUsers(email: String, usersToUpdate: List<AdminDTO>): List<AdminDTO> {
        verifyUserIsSuperAdmin(email)

        val updatedUserList = mutableListOf<Users>()

        for (changedUser in usersToUpdate) {
            val user = findUserInDatabaseByEmail(changedUser.email)

            if (user.appRole == AppRole.SUPERADMIN) continue

            val newAppRole = when (changedUser.appRole) {
                "USER" -> AppRole.USER
                "ADMIN" -> AppRole.ADMIN
                else -> throw InvalidRoleException("AppRole ${changedUser.appRole} is not a valid role")
            }

            if (user.appRole != newAppRole) {
                user.appRole = newAppRole
                usersDB.save(user)
                updatedUserList.add(user)
            }
        }

        return updatedUserList.map { AdminDTO.of(it) }
    }

    override fun getDashboardHtml(email: String, instanceName: String, relativeRequestPath: String): Resource {
        val user = findUserInDatabaseByEmail(email)

        val userInCourse = user.userInCourse.firstOrNull { it.course?.instanceName == instanceName }
        if (userInCourse == null) {
            throw UserNotInCourseException("User with email $email is not in a course with instanceName $instanceName")
        }

        val userRole = userInCourse.courseRole?.name ?: throw CourseNotFoundException("Could not find course with code $instanceName")
        val courseCode = userInCourse.course?.courseCode ?: throw CourseNotFoundException("Could not find course with code $instanceName")
        return fileFetcherService.fetchDashboardHtml(email, userRole, courseCode, instanceName, relativeRequestPath)
    }

    override fun refreshUsersAndCourses() {
        val resource = fileFetcherService.fetchCsvFile()
        val records = fileParserService.parseFile(resource)

//        we ensure userInCourse records get removed from the DB through orphanRemoval = true
        usersDB.findAll().forEach { it.userInCourse.clear() }
        courseDB.findAll().forEach { it.userInCourse.clear() }

        courseDB.deleteAll()
//        when refreshing users in the database, we preserve existing ADMIN and SUPERADMIN users
        usersDB.deleteAllByAppRole(AppRole.USER)
        clearPersistenceContext()

        val usersCache = mutableMapOf<String, Users>()
        val courseCache = mutableMapOf<Int, Course>()
        val userInCourseList = mutableListOf<UserInCourse>()
        linkUsersAndCourses(records, usersCache, courseCache, userInCourseList)

        courseDB.saveAll(courseCache.values)
        usersDB.saveAll(usersCache.values)
        userInCourseDB.saveAll(userInCourseList)
    }

    private fun clearPersistenceContext() {
        entityManager.flush()
        entityManager.clear()
    }

    private fun findUserInDatabaseByEmail(email: String): Users {
        val lowercaseEmail = email.lowercase()
        val user =
            usersDB.findByIdOrNull(lowercaseEmail) ?: throw UserNotFoundException("User with email $email not found")
        return user
    }

    private fun verifyUserIsSuperAdmin(email: String) {
        val requestUser = findUserInDatabaseByEmail(email)
        if (requestUser.appRole != AppRole.SUPERADMIN) {
            throw UserNotAuthorizedException("User with $email does not have the authorization to make this request")
        }
    }

    private fun linkUsersAndCourses(
        records: List<List<String>>,
        usersCache: MutableMap<String, Users>,
        courseCache: MutableMap<Int, Course>,
        userInCourseList: MutableList<UserInCourse>
    ) {
        for (record in records) {
            val email = record[CsvColumns.USER_EMAIL]
            if (email.isBlank() || email.lowercase() == "null") continue
            val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()
            val courseRole = record[CsvColumns.COURSE_ROLE]

//            when refreshing users in the database, we preserve existing ADMIN and SUPERADMIN users
            val user = usersCache.getOrPut(email) {
                usersDB.findByIdOrNull(email.lowercase()) ?: convertToUser(record)
            }
            val course = courseCache.getOrPut(canvasCourseId) { convertToCourse(record) }

            val link = UserInCourse.createAndLink(user, course, parseCourseRole(courseRole))
            userInCourseList.add(link)
        }
    }

    private fun parseCourseRole(role: String): CourseRole =
        when (role.trim().uppercase()) {
            "STUDENT" -> CourseRole.STUDENT
            "TEACHER" -> CourseRole.TEACHER
            else -> throw InvalidRoleException("Invalid course role: $role")
        }

    private fun convertToCourse(record: List<String>): Course {
        val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()
        val courseName = record[CsvColumns.COURSE_NAME]
        val courseCode = record[CsvColumns.COURSE_CODE]
        val instanceName = record[CsvColumns.INSTANCE_NAME]
        val startDate = LocalDate.parse(record[CsvColumns.START_DATE].substring(0, 10))
        val endDate = LocalDate.parse(record[CsvColumns.END_DATE].substring(0, 10))

        return Course.of(canvasCourseId, courseName, courseCode, instanceName, startDate, endDate)
    }

    private fun convertToUser(record: List<String>): Users {
        val name = record[CsvColumns.USER_NAME]
        val email = record[CsvColumns.USER_EMAIL]

        return Users.of(email, name)
    }

    private object CsvColumns {
        const val CANVAS_COURSE_ID = 0
        const val COURSE_CODE = 1
        const val COURSE_NAME = 2
        const val INSTANCE_NAME = 3
        const val START_DATE = 4
        const val END_DATE = 5
        const val USER_NAME = 6
        const val USER_EMAIL = 7
        const val COURSE_ROLE = 8
    }
}