package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.Role
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.dashboard.domain.exception.UserNotFoundException
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherService
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito.*
import org.springframework.core.io.ByteArrayResource
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

class DashboardServiceImplTest {
    private lateinit var courseDB: CourseRepository
    private lateinit var usersDB: UsersRepository
    private lateinit var fileParserService: FileParserService
    private lateinit var fileFetcherService: FileFetcherService
    private lateinit var service: DashboardServiceImpl

    private lateinit var course50304: Course
    private lateinit var course9999: Course
    private lateinit var parsedRecords: List<List<String>>
    private val mockResource = ByteArrayResource(ByteArray(0))


    @BeforeEach
    fun setUp() {
        courseDB = mock()
        usersDB = mock()
        fileParserService = mock()
        fileFetcherService = mock()
        service = DashboardServiceImpl(courseDB, usersDB, fileParserService, fileFetcherService)

        course50304 = Course.of(50304, "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", LocalDate.parse("2025-09-01"), LocalDate.parse("2026-01-30"))
        course9999 = Course.of(9999, "Test cursus - September 2010", "TEST-9999_SEP25", LocalDate.parse("2010-09-01"), LocalDate.parse("2011-01-30"))
        parsedRecords = listOf(
            listOf(
                "50304", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "9999", "Test cursus - September 2010", "TEST-9999_SEP25", "2010-09-01 00:00:00+02:00", "2011-01-30 23:59:59+01:00", "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "50304", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "Jane Doe", "jane.doe@hu.nl", "TEACHER"
            ),
            listOf(
                "50304", "Innovation Semester - September 2025", "TICT-V3SE6-25_SEP25", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "User Null", "null", "STUDENT"
            ),
            listOf(
                "9999", "Test cursus - September 2010", "TEST-9999_SEP25", "2010-09-01 00:00:00+02:00", "2011-01-30 23:59:59+01:00", "Test User", "test.user@student.hu.nl", "STUDENT"
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
        val user = Users.of("john.doe@student.hu.nl", "John Doe", Role.STUDENT)
        `when`(usersDB.findById("john.doe@student.hu.nl")).thenReturn(Optional.of(user))
        val actualDTO = service.findUserByEmail("john.doe@student.hu.nl")

        assertNotNull(actualDTO)
        val expectedDTO = UsersDTO(email = "john.doe@student.hu.nl", name = "John Doe", role = "STUDENT")
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
    fun refreshUsersAndCourses_persistsEachUniqueCourseOnce() {
        service.refreshUsersAndCourses()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            courses.count { it.canvasCourseId == 50304 } == 1 &&
                    courses.count { it.canvasCourseId == 9999 } == 1
        })
    }

    @Test
    fun refreshUsersAndCourses_persistsEachUniqueUserOnce() {
        service.refreshUsersAndCourses()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            users.count { it.email == "john.doe@student.hu.nl"} == 1 &&
                    users.count { it.email == "jane.doe@hu.nl"} == 1 &&
                    users.count { it.email == "test.user@student.hu.nl"} == 1
        })
    }

    @Test
    fun refreshUsersAndCourses_persistsAllUniqueUsersWithTheirCourses() {
        service.refreshUsersAndCourses()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            val userJohn = users.find { it.email == "john.doe@student.hu.nl"}
            val userJane = users.find { it.email == "jane.doe@hu.nl"}
            val userTest = users.find { it.email == "test.user@student.hu.nl" }
            userJohn != null && userJane != null && userTest != null &&
                    userJohn.courses.map { it.canvasCourseId }.toSet() == setOf(50304, 9999) &&
                    userJane.courses.map { it.canvasCourseId }.toSet() == setOf(50304) &&
                    userTest.courses.map { it.canvasCourseId }.toSet() == setOf(9999)
        })
    }

    @Test
    fun refreshUsersAndCourses_persistsAllUniqueCoursesWithTheirUsers() {
        service.refreshUsersAndCourses()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            val course1 = courses.find { it.canvasCourseId == 50304 }
            val course2 = courses.find { it.canvasCourseId == 9999 }
            course1 != null && course2 != null &&
                    course1.users.map { it.email }.toSet() == setOf("john.doe@student.hu.nl", "jane.doe@hu.nl") &&
                    course2.users.map { it.email }.toSet() == setOf("john.doe@student.hu.nl", "test.user@student.hu.nl")
        })
    }

    @Test
    fun refreshUsersAndCourses_userCanHaveMultipleCourses() {
        service.refreshUsersAndCourses()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            val user = users.find { it.email == "john.doe@student.hu.nl" }
            user != null && user.courses.size == 2
        })
    }

    @Test
    fun refreshUsersAndCourses_courseCanHaveMultipleUsers() {
        service.refreshUsersAndCourses()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            val course = courses.find { it.canvasCourseId == 50304 }
            course != null && course.users.size == 2
        })
    }

    @Test
    fun refreshUsersAndCourses_skipsUsersWithNullEmailAddress() {
        service.refreshUsersAndCourses()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            users.none { it.email == "null"}
        })
    }
}