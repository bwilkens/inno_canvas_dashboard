package nl.hu.inno.dashboard.dashboard.application

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import nl.hu.inno.dashboard.dashboard.application.dto.AdminDTO
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UserInCourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.*
import nl.hu.inno.dashboard.exception.exceptions.*
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherService
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.springframework.core.io.Resource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import org.slf4j.LoggerFactory

@Service
@Transactional
class DashboardServiceImpl(
    private val courseDb: CourseRepository,
    private val usersDb: UsersRepository,
    private val userInCourseDb: UserInCourseRepository,
    private val fileParserService: FileParserService,
    private val fileFetcherService: FileFetcherService,
    @PersistenceContext private val entityManager: EntityManager,
) : DashboardService {

    override fun findUserByEmail(email: String): UsersDTO {
        val user = findUserInDatabaseByEmail(email)
        return UsersDTO.of(user)
    }

    override fun findAllAdmins(email: String): List<AdminDTO> {
        log.info("findAllAdmins requested by email={}", email)
        verifyUserIsSuperAdmin(email)

        val adminEmailSuffix = "@hu.nl"
        val adminRoles = listOf(AppRole.ADMIN, AppRole.SUPERADMIN)
        val adminList = usersDb.findAllAdminCandidates(adminRoles, adminEmailSuffix)

        return adminList.map { AdminDTO.of(it) }
    }

    override fun updateAdminUserRoles(email: String, usersToUpdate: List<AdminDTO>): List<AdminDTO> {
        log.info("updateAdminUserRoles requested by email={}, usersToUpdateCount={}", email, usersToUpdate.size)
        verifyUserIsSuperAdmin(email)

        val updatedUserList = mutableListOf<Users>()
        updateUserRoles(usersToUpdate, updatedUserList)
        usersDb.saveAll(updatedUserList)

        return updatedUserList.map { AdminDTO.of(it) }
    }

    override fun verifyUserIsAdminOrSuperAdmin(email: String) {
        val requestUser = findUserInDatabaseByEmail(email)
        if (requestUser.appRole != AppRole.SUPERADMIN && requestUser.appRole != AppRole.ADMIN) {
            log.warn("Authorization failed: email={}, required=ADMIN|SUPERADMIN, actual={}", email, requestUser.appRole)
            throw UserNotAuthorizedException("User with $email does not have the authorization to make this request")
        }
        log.debug("Authorization OK: email={}, role={}", email, requestUser.appRole)
    }

    override fun getDashboardHtml(email: String, instanceName: String, relativeRequestPath: String): Resource {
        val user = findUserInDatabaseByEmail(email)

        val userInCourse = user.userInCourse.firstOrNull { it.course?.instanceName == instanceName }
        if (userInCourse == null) {
            log.warn("User not in course: email={}, instanceName={}", email, instanceName)
            throw UserNotInCourseException("User with email $email is not in a course with instanceName $instanceName")
        }

        val userRole = userInCourse.courseRole?.name
            ?: run {
                log.error("CourseRole missing: email={}, instanceName={}", email, instanceName)
                throw InvalidRoleException("Could not find courseRole for user with email $email in course $instanceName")
            }

        val courseCode = userInCourse.course?.courseCode
            ?: run {
                log.error("Course missing: email={}, instanceName={}", email, instanceName)
                throw CourseNotFoundException("Could not find course with code $instanceName")
            }

        return fileFetcherService.fetchDashboardHtml(email, userRole, courseCode, instanceName, relativeRequestPath)
    }

    override fun refreshUsersAndCoursesWithRoleCheck(email: String) {
//        entry point to refreshUsersAndCourses from REST API (admin portal)
        log.info("refreshUsersAndCoursesWithRoleCheck requested by email={}", email)
        verifyUserIsAdminOrSuperAdmin(email)
        refreshUsersAndCourses()
    }

    override fun refreshUsersAndCoursesInternal() {
//        entry point to refreshUsersAndCourses from FileMonitor component
        refreshUsersAndCourses()
    }

    private fun refreshUsersAndCourses() {
        val userDataCsvFile = fileFetcherService.fetchCsvFile()
        val parsedRecords = fileParserService.parseFile(userDataCsvFile)

//        ensure UserInCourse associations are deleted first
        userInCourseDb.deleteAllUserInCourseRecords()
        courseDb.deleteAll()
//        when refreshing users in the database, we preserve existing ADMIN and SUPERADMIN users
        usersDb.deleteAllByAppRole(AppRole.USER)
        clearPersistenceContext()

//        create Users and Course objects and persist them
        val usersCache = mutableMapOf<String, Users>()
        val courseCache = mutableMapOf<Int, Course>()
        createUsersAndCoursesFromCsvRecords(parsedRecords, usersCache, courseCache)
        courseDb.saveAll(courseCache.values)
        usersDb.saveAll(usersCache.values)

//        add the UserInCourse associations and persist them
        val userInCourseList = createUserInCourseAssociations(parsedRecords, usersCache, courseCache)
        userInCourseDb.saveAll(userInCourseList)
    }

    private fun createUsersAndCoursesFromCsvRecords(
        parsedRecords: List<List<String>>,
        usersCache: MutableMap<String, Users>,
        courseCache: MutableMap<Int, Course>
    ) {
        for (record in parsedRecords) {
            val email = record[CsvColumns.USER_EMAIL]
            if (email.isBlank() || email.lowercase() == "null") continue
            val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()

//            when refreshing users in the database, we preserve existing ADMIN and SUPERADMIN users
            usersCache.getOrPut(email) {
                usersDb.findByIdOrNull(email.lowercase()) ?: convertToUser(record)
            }
            courseCache.getOrPut(canvasCourseId) { convertToCourse(record) }
        }
    }

    private fun createUserInCourseAssociations(
        parsedRecords: List<List<String>>,
        usersCache: MutableMap<String, Users>,
        courseCache: MutableMap<Int, Course>
    ): MutableList<UserInCourse> {
        val userInCourseList = mutableListOf<UserInCourse>()
        for (record in parsedRecords) {
            val email = record[CsvColumns.USER_EMAIL]
            if (email.isBlank() || email.lowercase() == "null") continue
            val canvasCourseId = record[CsvColumns.CANVAS_COURSE_ID].toInt()
            val courseRole = record[CsvColumns.COURSE_ROLE]

            val user = usersCache[email]!!
            val course = courseCache[canvasCourseId]!!
            val link = UserInCourse.createAndLink(user, course, parseCourseRole(courseRole))
            userInCourseList.add(link)
        }
        return userInCourseList
    }

    private fun clearPersistenceContext() {
        entityManager.flush()
        entityManager.clear()
    }

    private fun findUserInDatabaseByEmail(email: String): Users {
        val lowercaseEmail = email.lowercase()
        val user = usersDb.findByIdOrNull(lowercaseEmail)
        if (user == null) {
            log.warn("User not found: email={}", email)
            throw UserNotFoundException("User with email $email not found")
        }

        return user
    }

    private fun updateUserRoles(
        usersToUpdate: List<AdminDTO>,
        updatedUserList: MutableList<Users>
    ) {
        for (changedUser in usersToUpdate) {
            val user = findUserInDatabaseByEmail(changedUser.email)

//            ensure SUPERADMIN's cannot lose their role
            if (user.appRole == AppRole.SUPERADMIN) {
                log.warn("Attempt to change SUPERADMIN ignored: targetEmail={}", changedUser.email)
                continue
            }

            val newAppRole = when (changedUser.appRole) {
                "USER" -> AppRole.USER
                "ADMIN" -> AppRole.ADMIN
                else -> {
                    log.warn("Invalid role provided: targetEmail={}, role={}", changedUser.email, changedUser.appRole)
                    throw InvalidRoleException("AppRole ${changedUser.appRole} is not a valid role")
                }
            }

//            only update user if their role changed
            if (user.appRole != newAppRole) {
                log.info("Updating role: email={}, from={}, to={}", user.email, user.appRole, newAppRole)
                user.appRole = newAppRole
                updatedUserList.add(user)
            }
        }
    }

    private fun verifyUserIsSuperAdmin(email: String) {
        val requestUser = findUserInDatabaseByEmail(email)
        if (requestUser.appRole != AppRole.SUPERADMIN) {
            log.warn("SuperAdmin check failed: email={}, actualRole={}", email, requestUser.appRole)
            throw UserNotAuthorizedException("User with $email does not have the authorization to make this request")
        }
        log.debug("SuperAdmin check OK: email={}", email)
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

    companion object {
        private val log = LoggerFactory.getLogger(DashboardServiceImpl::class.java)
    }
}