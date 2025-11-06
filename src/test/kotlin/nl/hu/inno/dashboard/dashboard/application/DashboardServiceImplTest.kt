package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.Users
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherService
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito.*
import org.springframework.core.io.ByteArrayResource
import java.time.LocalDate
import java.util.*

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

        course50304 = Course.of(50304, "Innovation Semester - September 2025", LocalDate.parse("2025-09-01"), LocalDate.parse("2026-01-30"))
        course9999 = Course.of(9999, "Test cursus - September 2010", LocalDate.parse("2010-09-01"), LocalDate.parse("2011-01-30"))
        parsedRecords = listOf(
            listOf(
                "50304", "Innovation Semester - September 2025", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "9999", "Test cursus - September 2010", "2010-09-01 00:00:00+02:00", "2011-01-30 23:59:59+01:00", "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "50304", "Innovation Semester - September 2025", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "Jane Doe", "jane.doe@hu.nl", "TEACHER"
            ),
            listOf(
                "50304", "Innovation Semester - September 2025", "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00", "User Null", "null", "STUDENT"
            ),
            listOf(
                "9999", "Test cursus - September 2010", "2010-09-01 00:00:00+02:00", "2011-01-30 23:59:59+01:00", "Test User", "test.user@student.hu.nl", "STUDENT"
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
    fun addUsersToCourse_persistsEachUniqueCourseOnce() {
        service.addUsersToCourse()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            courses.count { it.canvasId == 50304 } == 1 &&
                    courses.count { it.canvasId == 9999 } == 1
        })
    }

    @Test
    fun addUsersToCourse_persistsEachUniqueUserOnce() {
        service.addUsersToCourse()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            users.count { it.emailAddress == "john.doe@student.hu.nl"} == 1 &&
                    users.count { it.emailAddress == "jane.doe@hu.nl"} == 1 &&
                    users.count { it.emailAddress == "test.user@student.hu.nl"} == 1
        })
    }

    @Test
    fun addUsersToCourse_persistsAllUniqueUsersWithTheirCourses() {
        service.addUsersToCourse()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            val userJohn = users.find { it.emailAddress == "john.doe@student.hu.nl"}
            val userJane = users.find { it.emailAddress == "jane.doe@hu.nl"}
            val userTest = users.find { it.emailAddress == "test.user@student.hu.nl" }
            userJohn != null && userJane != null && userTest != null &&
                    userJohn.courses.map { it.canvasId }.toSet() == setOf(50304, 9999) &&
                    userJane.courses.map { it.canvasId }.toSet() == setOf(50304) &&
                    userTest.courses.map { it.canvasId }.toSet() == setOf(9999)
        })
    }

    @Test
    fun addUsersToCourse_persistsAllUniqueCoursesWithTheirUsers() {
        service.addUsersToCourse()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            val course1 = courses.find { it.canvasId == 50304 }
            val course2 = courses.find { it.canvasId == 9999 }
            course1 != null && course2 != null &&
                    course1.users.map { it.emailAddress }.toSet() == setOf("john.doe@student.hu.nl", "jane.doe@hu.nl") &&
                    course2.users.map { it.emailAddress }.toSet() == setOf("john.doe@student.hu.nl", "test.user@student.hu.nl")
        })
    }

    @Test
    fun addUsersToCourse_userCanHaveMultipleCourses() {
        service.addUsersToCourse()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            val user = users.find { it.emailAddress == "john.doe@student.hu.nl" }
            user != null && user.courses.size == 2
        })
    }

    @Test
    fun addUsersToCourse_courseCanHaveMultipleUsers() {
        service.addUsersToCourse()

        verify(courseDB).saveAll(argThat { courses: Collection<Course> ->
            val course = courses.find { it.canvasId == 50304 }
            course != null && course.users.size == 2
        })
    }

    @Test
    fun addUsersToCourse_skipsUsersWithNullEmailAddress() {
        service.addUsersToCourse()

        verify(usersDB).saveAll(argThat { users: Collection<Users> ->
            users.none { it.emailAddress == "null"}
        })
    }

    @Test
    @Disabled("Not implemented yet")
    fun updateUsersInCourse() {
    }
}