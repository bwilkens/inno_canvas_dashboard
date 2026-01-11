package nl.hu.inno.dashboard.dashboard.application

import jakarta.persistence.EntityManager
import nl.hu.inno.dashboard.dashboard.application.dto.AdminDTO
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UserInCourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.AppRole
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.CourseRole
import nl.hu.inno.dashboard.dashboard.domain.UserInCourse
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.exception.exceptions.UserNotAuthorizedException
import nl.hu.inno.dashboard.exception.exceptions.UserNotFoundException
import nl.hu.inno.dashboard.exception.exceptions.UserNotInCourseException
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherService
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito.*
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

class DashboardServiceImplTest {
    private lateinit var courseDB: CourseRepository
    private lateinit var usersDB: UsersRepository
    private lateinit var userInCourseDB: UserInCourseRepository
    private lateinit var fileParserService: FileParserService
    private lateinit var fileFetcherService: FileFetcherService
    private lateinit var service: DashboardServiceImpl
    private lateinit var entityManager: EntityManager

    private lateinit var course50304: Course
    private lateinit var course9999: Course
    private lateinit var parsedRecords: List<List<String>>
    private val mockResource = ByteArrayResource(ByteArray(0))


    @BeforeEach
    fun setUp() {
        courseDB = mock()
        usersDB = mock()
        userInCourseDB = mock()
        fileParserService = mock()
        fileFetcherService = mock()
        entityManager = mock()
        service = DashboardServiceImpl(courseDB, usersDB, userInCourseDB, fileParserService, fileFetcherService, entityManager)

        course50304 = Course.of(50304, "Innovation Semester - September 2025", "TICT-V3SE6-25", "TICT-V3SE6-25_SEP25", LocalDate.parse("2025-09-01"), LocalDate.parse("2026-01-30"))
        course9999 = Course.of(9999, "Test cursus - September 2010", "TEST-9999", "TEST-9999_SEP25", LocalDate.parse("2010-09-01"), LocalDate.parse("2011-01-30"))
        parsedRecords = listOf(
            listOf(
                "50304", "TICT-V3SE6-25", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "9999", "TEST-9999", "Test cursus - September 2010", "TEST-9999_SEP25", "2010-09-01 00:00:00+02:00", "2011-01-30 23:59:59+01:00", "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "50304", "TICT-V3SE6-25", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "Jane Doe", "jane.doe@hu.nl", "TEACHER"
            ),
            listOf(
                "50304", "TICT-V3SE6-25", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "User Null", "null", "STUDENT"
            ),
            listOf(
                "9999", "TEST-9999", "Test cursus - September 2010", "TEST-9999_SEP25", "2010-09-01 00:00:00+02:00", "2011-01-30 23:59:59+01:00", "Test User", "test.user@student.hu.nl", "STUDENT"
            ),
            listOf(
                "50304", "TICT-V3SE6-25", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "Blank User", "", "STUDENT"
            )
        )

        `when`(fileFetcherService.fetchCsvFile()).thenReturn(mockResource)
        `when`(fileParserService.parseFile(mockResource)).thenReturn(parsedRecords)
        `when`(courseDB.findById(50304)).thenReturn(Optional.of(course50304))
        `when`(courseDB.findById(9999)).thenReturn(Optional.of(course9999))
        `when`(courseDB.saveAll(anyList())).thenAnswer { it.getArgument(0) }
        `when`(usersDB.saveAll(anyList())).thenAnswer { it.getArgument(0) }
    }

    @Test
    fun findUserByEmail_returnsUsersDTO_whenUserExists() {
        val user = Users.of("john.doe@student.hu.nl", "John Doe")
        `when`(usersDB.findById("john.doe@student.hu.nl")).thenReturn(Optional.of(user))

        val actualDTO = service.findUserByEmail("john.doe@student.hu.nl")

        assertNotNull(actualDTO)
        val expectedDTO = UsersDTO(email = "john.doe@student.hu.nl", name = "John Doe", appRole = "USER")
        assertEquals(expectedDTO, actualDTO)
    }

    @Test
    fun findUserByEmail_throwsException_whenUserDoesNotExist() {
        `when`(usersDB.findById("not.exists@hu.nl")).thenReturn(Optional.empty())

        val exception = assertThrows<UserNotFoundException> {
            service.findUserByEmail("not.exists@hu.nl")
        }
        assertEquals("User with email not.exists@hu.nl not found", exception.message)
    }

    @Test
    fun findAllAdmins_returnsAdminDTOs_whenSuperAdminRequests() {
        val superAdmin = Users.of("super.admin@hu.nl", "Super Admin").apply { appRole = AppRole.SUPERADMIN }
        val admin1 = Users.of("admin1@hu.nl", "Admin One").apply { appRole = AppRole.ADMIN }
        val admin2 = Users.of("admin2@hu.nl", "Admin Two").apply { appRole = AppRole.ADMIN }
        val adminList = listOf(admin1, admin2)

        `when`(usersDB.findById("super.admin@hu.nl")).thenReturn(Optional.of(superAdmin))
        `when`(usersDB.findAllAdminCandidates(listOf(AppRole.ADMIN, AppRole.SUPERADMIN),"@hu.nl")).thenReturn(adminList)

        val result = service.findAllAdmins("super.admin@hu.nl")

        assertEquals(2, result.size)
        assertEquals("admin1@hu.nl", result[0].email)
        assertEquals("admin2@hu.nl", result[1].email)
    }

    @Test
    fun findAllAdmins_throwsUserNotAuthorizedException_whenNotSuperAdmin() {
        val admin = Users.of("admin@hu.nl", "Admin").apply { appRole = AppRole.ADMIN }
        `when`(usersDB.findById("admin@hu.nl")).thenReturn(Optional.of(admin))

        val exception = assertThrows<UserNotAuthorizedException> {
            service.findAllAdmins("admin@hu.nl")
        }
        assertEquals("User with admin@hu.nl does not have the authorization to make this request", exception.message)
    }

    @Test
    fun updateAdminUsers_updatesRoles_whenSuperAdminRequests() {
        val superAdmin = Users.of("super.admin@hu.nl", "Super Admin").apply { appRole = AppRole.SUPERADMIN }
        val admin = Users.of("admin@hu.nl", "Admin").apply { appRole = AppRole.ADMIN }
        val user = Users.of("user@hu.nl", "User").apply { appRole = AppRole.USER }

        val adminDTO = AdminDTO(email = "admin@hu.nl", name = "Admin", appRole = "USER")
        val userDTO = AdminDTO(email = "user@hu.nl", name = "User", appRole = "ADMIN")
        val superAdminDTO = AdminDTO(email = "super.admin@hu.nl", name = "Super Admin", appRole = "USER")

        `when`(usersDB.findById("super.admin@hu.nl")).thenReturn(Optional.of(superAdmin))
        `when`(usersDB.findById("admin@hu.nl")).thenReturn(Optional.of(admin))
        `when`(usersDB.findById("user@hu.nl")).thenReturn( Optional.of(user))
        `when`(usersDB.findById("super.admin@hu.nl")).thenReturn(Optional.of(superAdmin))
        `when`(usersDB.save(any(Users::class.java))).thenAnswer { it.getArgument(0) }

        val result = service.updateAdminUserRoles("super.admin@hu.nl", listOf(adminDTO, userDTO, superAdminDTO))

        assertEquals(2, result.size)
        assertTrue(result.any { it.email == "admin@hu.nl" && it.appRole == "USER" })
        assertTrue(result.any { it.email == "user@hu.nl" && it.appRole == "ADMIN" })
        assertFalse(result.any { it.email == "super.admin@hu.nl" })
    }

    @Test
    fun updateAdminUsers_throwsUserNotAuthorizedException_whenNotSuperAdmin() {
        val admin = Users.of("admin@hu.nl", "Admin").apply { appRole = AppRole.ADMIN }
        val adminDTO = AdminDTO(email = "admin@hu.nl", name = "Admin", appRole = "USER")
        `when`(usersDB.findById("admin@hu.nl")).thenReturn(Optional.of(admin))

        val exception = assertThrows<UserNotAuthorizedException> {
            service.updateAdminUserRoles("admin@hu.nl", listOf(adminDTO))
        }
        assertEquals("User with admin@hu.nl does not have the authorization to make this request", exception.message)
    }

    @Test
    fun updateAdminUsers_doesNotUpdateIfRoleIsSame() {
        val superAdmin = Users.of("super.admin@hu.nl", "Super Admin").apply { appRole = AppRole.SUPERADMIN }
        val admin = Users.of("admin@hu.nl", "Admin").apply { appRole = AppRole.ADMIN }
        val adminDTO = AdminDTO(email = "admin@hu.nl", name = "Admin", appRole = "ADMIN")

        `when`(usersDB.findById("super.admin@hu.nl")).thenReturn(Optional.of(superAdmin))
        `when`(usersDB.findById("admin@hu.nl")).thenReturn(Optional.of(admin))

        val result = service.updateAdminUserRoles("super.admin@hu.nl", listOf(adminDTO))

        assertTrue(result.isEmpty())
        verify(usersDB, never()).save(admin)
    }

    @Test
    fun getDashboardHtml_returnsResource_whenUserInCourse() {
        val user = Users.of("john.doe@student.hu.nl", "John Doe")
        val course = Course.of(50304, "Innovation Semester - September 2025", "TICT-V3SE6-25", "TICT-V3SE6-25_SEP25", LocalDate.parse("2025-09-01"), LocalDate.parse("2026-01-30"))
        UserInCourse.createAndLink(user, course, CourseRole.valueOf("STUDENT"))
        `when`(usersDB.findById("john.doe@student.hu.nl")).thenReturn(Optional.of(user))
        val expectedResult = mock(Resource::class.java)
        `when`(fileFetcherService.fetchDashboardHtml("john.doe@student.hu.nl", "STUDENT", "TICT-V3SE6-25", "TICT-V3SE6-25_SEP25", "/dashboard")).thenReturn(expectedResult)

        val actualResult = service.getDashboardHtml("john.doe@student.hu.nl", "TICT-V3SE6-25_SEP25", "/dashboard")

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getDashboardHtml_throwsUserNotInCourseException_whenUserNotInCourse() {
        val user = Users.of("john.doe@student.hu.nl", "John Doe")
        `when`(usersDB.findById("john.doe@student.hu.nl")).thenReturn(Optional.of(user))

        val actualMessage = assertThrows<UserNotInCourseException> {
            service.getDashboardHtml("john.doe@student.hu.nl", "SOME_OTHER_INSTANCE", "/dashboard")
        }

        val expectedMessage = "User with email john.doe@student.hu.nl is not in a course with instanceName SOME_OTHER_INSTANCE"
        assertEquals(expectedMessage, actualMessage.message)
    }

    @Test
    fun getDashboardHtml_throwsUserNotFoundException_whenUserDoesNotExist() {
        `when`(usersDB.findById("not.exists@hu.nl")).thenReturn(Optional.empty())

        val actualMessage = assertThrows<UserNotFoundException> {
            service.getDashboardHtml("not.exists@hu.nl", "TICT-V3SE6-25_SEP25", "/dashboard")
        }

        val expectedMessage = "User with email not.exists@hu.nl not found"
        assertEquals(expectedMessage, actualMessage.message)
    }

    @Test
    fun refreshUsersAndCourses_persistsEachUniqueCourseOnce() {
        service.refreshUsersAndCoursesInternal()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            courses.count { it.canvasCourseId == 50304 } == 1 &&
                    courses.count { it.canvasCourseId == 9999 } == 1
        })
    }

    @Test
    fun refreshUsersAndCourses_persistsEachUniqueUserOnce() {
        service.refreshUsersAndCoursesInternal()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            users.count { it.email == "john.doe@student.hu.nl"} == 1 &&
                    users.count { it.email == "jane.doe@hu.nl"} == 1 &&
                    users.count { it.email == "test.user@student.hu.nl"} == 1
        })
    }

    @Test
    fun refreshUsersAndCourses_persistsAllUniqueUsersWithTheirCourses() {
        service.refreshUsersAndCoursesInternal()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            val userJohn = users.find { it.email == "john.doe@student.hu.nl"}
            val userJane = users.find { it.email == "jane.doe@hu.nl"}
            val userTest = users.find { it.email == "test.user@student.hu.nl" }
            userJohn != null && userJane != null && userTest != null &&
                    userJohn.userInCourse.mapNotNull { it.course?.canvasCourseId }.toSet() == setOf(50304, 9999) &&
                    userJane.userInCourse.mapNotNull { it.course?.canvasCourseId }.toSet() == setOf(50304) &&
                    userTest.userInCourse.mapNotNull { it.course?.canvasCourseId }.toSet() == setOf(9999)
        })
    }

    @Test
    fun refreshUsersAndCourses_persistsAllUniqueCoursesWithTheirUsers() {
        service.refreshUsersAndCoursesInternal()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            val course1 = courses.find { it.canvasCourseId == 50304 }
            val course2 = courses.find { it.canvasCourseId == 9999 }
            course1 != null && course2 != null &&
                    course1.userInCourse.mapNotNull { it.user?.email }.toSet() == setOf("john.doe@student.hu.nl", "jane.doe@hu.nl") &&
                    course2.userInCourse.mapNotNull { it.user?.email }.toSet() == setOf("john.doe@student.hu.nl", "test.user@student.hu.nl")
        })
    }

    @Test
    fun refreshUsersAndCourses_userCanHaveMultipleCourses() {
        service.refreshUsersAndCoursesInternal()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            val user = users.find { it.email == "john.doe@student.hu.nl" }
            user != null && user.userInCourse.size == 2
        })
    }

    @Test
    fun refreshUsersAndCourses_courseCanHaveMultipleUsers() {
        service.refreshUsersAndCoursesInternal()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            val course = courses.find { it.canvasCourseId == 50304 }
            course != null && course.userInCourse.size == 2
        })
    }

    @Test
    fun refreshUsersAndCourses_skipsUsersWithNullEmailAddress() {
        service.refreshUsersAndCoursesInternal()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            users.none { it.email == "null"}
        })
    }
}